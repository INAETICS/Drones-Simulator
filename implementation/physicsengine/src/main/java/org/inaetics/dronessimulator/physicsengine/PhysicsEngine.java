package org.inaetics.dronessimulator.physicsengine;

import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.physicsengine.entityupdate.EntityUpdate;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PhysicsEngine extends Thread {
    public static final D3Vector GRAVITY = new D3Vector(0, 0, 0);

    private long current_step_started_at_ms;
    private long last_state_broadcast_at_ms;
    private long broadcast_state_every_ms;

    private boolean quit;
    private final AtomicBoolean started;

    private final EntityManager entityManager;

    private final HashMap<Integer, Integer> currentCollisions;

    private PhysicsEngineEventObserver observer;

    public PhysicsEngine() {
        this.current_step_started_at_ms = System.currentTimeMillis();
        this.last_state_broadcast_at_ms = this.current_step_started_at_ms;
        this.broadcast_state_every_ms = -1;

        this.quit = false;
        this.started = new AtomicBoolean(false);

        this.entityManager = new EntityManager();

        this.currentCollisions = new HashMap<>();

        this.observer = null;
    }

    public void setObserver(PhysicsEngineEventObserver observer) {
        this.observer = observer;
    }

    public void setTimeBetweenBroadcastms(long broadcast_state_every_ms) {
        this.broadcast_state_every_ms = broadcast_state_every_ms;
    }

    private D3Vector environmentForces(Entity entity) {
        return GRAVITY;
    }

    private double stageTimeStep() {
        long current_ms = System.currentTimeMillis();
        long timestep_ms = current_ms - current_step_started_at_ms;
        double timestep_s = ((float) timestep_ms) / 1000;
        this.current_step_started_at_ms = current_ms;

        return timestep_s;
    }

    private void stageMove(double timestep_s) {
        Map<Integer, Entity> entities = this.entityManager.getEntities();

        for(Map.Entry<Integer, Entity> e1 : entities.entrySet()) {
            Entity entity = e1.getValue();
            int e1Id = entity.getId();

            // Set the next place the entity will move to with new velocity
            D3Vector nextAcceleration = entity.getAcceleration();
            D3Vector nextVelocity = entity.nextVelocity(environmentForces(entity).add(nextAcceleration), timestep_s);
            D3Vector nextPosition = entity.nextPosition(nextVelocity, timestep_s);

            entity.setAcceleration(nextAcceleration);
            entity.setVelocity(nextVelocity);
            entity.setPosition(nextPosition);

            // Check for collisions for this entity
            for(Map.Entry<Integer, Entity> e2 : entities.entrySet()) {
                Entity otherEntity = e2.getValue();
                int e2Id = otherEntity.getId();

                // If the entity is colliding with another entity
                if(!entity.equals(otherEntity) && entity.collides(otherEntity)) {
                    // Only add to hashmap if the collision was not present yet
                    Integer e1CollidedWithe2 = currentCollisions.putIfAbsent(e1Id, e2Id);
                    Integer e2CollidedWithe1 = currentCollisions.putIfAbsent(e2Id, e1Id);

                    // If the collision wasn't happening yet
                    if(observer != null && (e1CollidedWithe2 == null || e2CollidedWithe1 == null)) {
                        //This collision is new and has just started
                        observer.collisionStartHandler(Entity.copy(entity), Entity.copy(otherEntity));
                    }
                } else {
                    //These entities are not colliding, so remove any collisions if there were any
                    Integer e1CollidedWithe2 = currentCollisions.remove(e1Id);
                    Integer e2CollidedWithe1 = currentCollisions.remove(e2Id);

                    if(e1CollidedWithe2 != null || e2CollidedWithe1 != null) {
                        //This collision has just ended
                        observer.collisionStopHandler(Entity.copy(entity), Entity.copy(otherEntity));
                    }
                }
            }


        }
    }

    private void stageBroadcastState() {
        long last_broadcast_ms = this.current_step_started_at_ms - this.last_state_broadcast_at_ms;

        if(this.broadcast_state_every_ms >= 0 && last_broadcast_ms >= this.broadcast_state_every_ms) {
            //System.out.println("PHYSICS ENGINE SHOULD BROADCAST");
            observer.broadcastStateHandler(this.entityManager.copyState());
            this.last_state_broadcast_at_ms = this.current_step_started_at_ms;
        }

    }

    private void runServer() {
        Thread t = Thread.currentThread();

        if(started.compareAndSet(false, true)) {
            quit = false;

            this.current_step_started_at_ms = System.currentTimeMillis();
            this.last_state_broadcast_at_ms = this.current_step_started_at_ms;
            
            while(!t.isInterrupted()) {
                double timestep_s = this.stageTimeStep();
                this.entityManager.processChanges();
                this.stageMove(timestep_s);
                this.stageBroadcastState();
            }

            started.set(false);
            quit = true;
        }
    }

    /*
        @thread-safe
     */
    public void run() {
        this.runServer();
    }

    /*
        @thread-safe
     */
    public void addInserts(Collection<EntityCreation> creations) {
        this.entityManager.addInserts(creations);
    }

    /*
        @thread-safe
     */
    public void addInsert(EntityCreation creation) {
        this.entityManager.addInsert(creation);
    }

    /*
        @thread-safe
     */
    public void addUpdates(Integer entityId, Collection<EntityUpdate> updates) {
        this.entityManager.addUpdates(entityId, updates);
    }

    /*
        @thread-safe
     */
    public void addUpdate(Integer entityId, EntityUpdate update) {
       this.entityManager.addUpdate(entityId, update);
    }

    /*
        @thread-safe
     */
    public void addRemovals(Collection<Integer> removals) {
        this.entityManager.addRemovals(removals);
     }

    /*
        @thread-safe
    */
    public void addRemoval(Integer removal) {
        this.entityManager.addRemoval(removal);
    }

    /*
        @thread-safe
     */
    public void quit() {
        this.interrupt();
    }

    /*
        @thread-safe
     */
     public boolean hasStarted() {
        return this.started.get();
     }

     /*
        @thread-safe
     */
     public boolean hasQuit() { return this.quit; }
}
