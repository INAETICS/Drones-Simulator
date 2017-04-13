package org.inaetics.dronessimulator.physicsengine;

import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.physicsengine.entityupdate.EntityUpdate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class PhysicsEngine extends Thread {
    public static final D3Vector GRAVITY = new D3Vector(0, 0, -9.81);

    private long last_step_at;
    private volatile boolean quit;
    private final AtomicBoolean started;

    private final EntityManager entityManager;


    public PhysicsEngine() {
        this.last_step_at = System.currentTimeMillis();
        this.quit = false;
        this.started = new AtomicBoolean(false);
        this.entityManager = new EntityManager();

        //Test data
        Collection<EntityCreation> testInserts = new ArrayList<>();
        testInserts.add(new EntityCreation(new Entity(1, new Size(1, 1, 1), true, new D3Vector(0, 0, 0), new D3Vector(1, 0, 0), new D3Vector(0, 0, 0))));
        testInserts.add(new EntityCreation(new Entity(2, new Size(1, 1, 1), true, new D3Vector(10, 0, 0), new D3Vector(0, 0, 0), new D3Vector(-1, 0, 0))));

        this.entityManager.addInserts(testInserts);
    }

    private D3Vector environmentForces(Entity entity) {
        return GRAVITY;
    }

    private D3Vector collionForce(Entity subject, Entity collision) {
        return null;
    }

    private void stageMove(double timestep_s) {
        Map<Integer, Entity> entities = this.entityManager.getEntitiesUnsafe();

        for(Map.Entry<Integer, Entity> e1 : entities.entrySet()) {
            Entity entity = e1.getValue();
            D3Vector nextAcceleration = entity.getAcceleration();
            D3Vector nextVelocity = entity.nextVelocity(environmentForces(entity).add(nextAcceleration), timestep_s);
            D3Vector nextPosition = entity.nextPosition(nextVelocity, timestep_s);

            if(entity.isCollideable()) {
                Entity newEntity = new Entity(entity.getId(), entity.getSize(), true, nextPosition, nextVelocity, nextAcceleration);
                boolean collides = false;
                Entity collidesWith;

                for(Map.Entry<Integer, Entity> e2 : entities.entrySet()) {
                    Entity otherEntity = e2.getValue();

                    if(!entity.equals(otherEntity) && newEntity.collides(otherEntity) && entity.isCollideable() && otherEntity.isCollideable()) {
                        collides = true;
                        collidesWith = otherEntity;
                        break;
                    }
                }

                if(!collides) {
                    entity.setAcceleration(nextAcceleration);
                    entity.setVelocity(nextVelocity);
                    entity.setPosition(nextPosition);
                } else {
                    // TODO do something with collision
                }
            }
        }
    }

    private void runServer() {
        if(started.compareAndSet(false, true)) {
            while(!quit) {
                synchronized(this.entityManager.entitiesLock()) {
                    long current_ms = System.currentTimeMillis();
                    long timestep_ms = current_ms - last_step_at;
                    double timestep_s = ((float) timestep_ms) / 1000;
                    this.last_step_at = current_ms;


                    this.entityManager.processChanges();
                    this.stageMove(timestep_s);
                }

                try {
                   Thread.sleep(100);
                } catch(InterruptedException e) {

                }
            }
        }
    }

    public void run() {
        this.runServer();
    }

    /*
        @thread-safe
     */
    public List<Entity> getCurrentState() {
        List<Entity> result;

        synchronized(this.entityManager.entitiesLock()) {
            result = this.entityManager.copyState();
        }

        return result;
    }

    public void addInserts(Collection<EntityCreation> creations) {
        this.entityManager.addInserts(creations);
    }

    public void addUpdates(Integer entityId, Collection<EntityUpdate> updates) {
        this.entityManager.addUpdates(entityId, updates);
    }

    public void addRemovals(Collection<Integer> removals) {
        this.entityManager.addRemovals(removals);
     }

    /*
        @thread-safe
     */
    public void quit() {
        this.quit = true;
    }

    /*
        @thread-safe
     */
     public boolean hasStarted() {
        return this.started.get();
     }
}
