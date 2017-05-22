package org.inaetics.dronessimulator.physicsengine;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.physicsengine.entityupdate.EntityUpdate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Logger;

/**
 * A very simple physicsengine where gravity holds, all entites are 1kg and without other
 * interacting forces(e.g. collision- and airfrictionforce). Collisions are detected by the
 * simple hitboxes computed by the Size of the entity. Every broadcast_ms the current state is
 * broadcast to the observer. The start and end of every collision are also broadcast to the observer.
 *
 * @threadsafe
 */
public class PhysicsEngine extends Thread implements IPhysicsEngine {
    /**
     * Gravityforce in meters / seconds^2
     */
    public static final D3Vector GRAVITY = new D3Vector(0, 0, 0); //-9.81);

    /**
     * At what time the current timestep started. Used as timestep to move all entities.
     * In milliseconds.
     */
    private long current_step_started_at_ms;
    /**
     * At what time the last state broadcast was send to observer. In milliseconds.
     */
    private long last_state_broadcast_at_ms;
    /**
     * How long between broadcasts of current state in milliseconds.
     */
    private long broadcast_state_every_ms;

    /**
     * If the physicsengine has quit. (Only true if the engine has started and then quit)
     */
    private volatile boolean quit;
    /**
     * If the physicsengine is started.
     */
    private final AtomicBoolean started;

    /**
     * The entity manager which manages all changes and state of entities
     */
    private final EntityManager entityManager;

    /**
     * A map containing all collisions between entity ids which have started
     */
    private final HashMap<Integer, Integer> currentCollisions;

    /**
     * The observer to which any events are send.
     */
    private PhysicsEngineEventObserver observer;

    /**
     * Create the physics engine object
     * Before you start the engine, you MUST set an observer using setObserver
     */
    public PhysicsEngine() {
        System.out.println("CREATED PHYSICS ENGINE");
        this.current_step_started_at_ms = System.currentTimeMillis();
        this.last_state_broadcast_at_ms = this.current_step_started_at_ms;
        this.broadcast_state_every_ms = -1;

        this.quit = false;
        this.started = new AtomicBoolean(false);

        this.entityManager = new EntityManager();

        this.currentCollisions = new HashMap<>();

        this.observer = null;
    }

    /**
     * Set the time between broadcasts. By default, state broadcasting is off.
     * @param broadcast_state_every_ms The time between broadcasts in milliseconds.
     */
    public void setTimeBetweenBroadcastms(long broadcast_state_every_ms) {
        this.broadcast_state_every_ms = broadcast_state_every_ms;
    }

    /**
     * Sets the observer for events for this engine
     * @param observer The object which will observer all engine events
     */
    public void setObserver(PhysicsEngineEventObserver observer) {
        this.observer = observer;
    }

    /**
     * Which environmentforces act on the entity. Currently only gravity
     * @param entity The entity on which environmentforces act
     * @return The total resulting vector of all environment forces.
     */
    private D3Vector environmentForces(Entity entity) {
        return GRAVITY;
    }

    /**
     * Determine the timestep for the current loop.
     * @return How long since the lastloop in seconds.
     */
    private double stageTimeStep() {
        long current_ms = System.currentTimeMillis();
        long timestep_ms = current_ms - current_step_started_at_ms;
        double timestep_s = ((float) timestep_ms) / 1000;
        this.current_step_started_at_ms = current_ms;

        return timestep_s;
    }

    /**
     * Move all entities respecting collisions. Entities can move through each other,
     * but collisions do spawn events send to the observer.
     * @param timestep_s How long since the last move in seconds.
     */
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
                        observer.collisionStartHandler(Entity.deepcopy(entity), Entity.deepcopy(otherEntity));
                    }
                } else {
                    //These entities are not colliding, so remove any collisions if there were any
                    Integer e1CollidedWithe2 = currentCollisions.remove(e1Id);
                    Integer e2CollidedWithe1 = currentCollisions.remove(e2Id);

                    if(e1CollidedWithe2 != null || e2CollidedWithe1 != null) {
                        //This collision has just ended
                        observer.collisionStopHandler(Entity.deepcopy(entity), Entity.deepcopy(otherEntity));
                    }
                }
            }


        }
    }

    /**
     * Broadcast the state if necessary for this loop
     */
    private void stageBroadcastState() {
        long last_broadcast_ms = this.current_step_started_at_ms - this.last_state_broadcast_at_ms;

        if(this.broadcast_state_every_ms >= 0 && last_broadcast_ms >= this.broadcast_state_every_ms) {
            observer.broadcastStateHandler(this.entityManager.copyState());
            this.last_state_broadcast_at_ms = this.current_step_started_at_ms;
        }

    }

    /**
     * Start the current physicsengine. If already started, it does not start.
     * @threadsafe
     */
    private void runServer() {
        Thread t = Thread.currentThread();

        if(started.compareAndSet(false, true)) {
            Logger.getLogger(PhysicsEngine.class).info("Started PhysicsEngine!");

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

            Logger.getLogger(PhysicsEngine.class).info("PhysicsEngine has shutdown");
        }
    }

    public void start() {
        Logger.getLogger(PhysicsEngine.class).info("Starting PhysicsEngine...");

        super.start();
    }

    /**
     * Method to start the Thread. Do not call directly, use PhysicsEngine.start().
     * @requires this.getObserver() != null
     * @threadsafe
     */
    public void run() {
        assert this.observer != null;

        this.runServer();
    }

    /**
     * Add any new entities to the physicsengine.
     * @threadsafe
     * @param creations Which entities to add.
     */
    public void addInserts(Collection<Entity> creations) {
        this.entityManager.addInserts(creations);
    }

    /**
     * Add the new entity to the physicsengine.
     * @threadsafe
     * @param creation Which entity to add.
     */
    public void addInsert(Entity creation) {
        this.entityManager.addInsert(creation);
    }

    /**
     * Change entity by applying the updates
     * @threafsafe
     * @param entityId Which entity to apply updates to
     * @param updates Which updates to apply
     */
    public void addUpdates(Integer entityId, Collection<EntityUpdate> updates) {
        this.entityManager.addUpdates(entityId, updates);
    }

    /**
     * Change entity by applying the update
     * @threadsafe
     * @param entityId Which entity to apply update to
     * @param update Which update to apply
     */
    public void addUpdate(Integer entityId, EntityUpdate update) {
       this.entityManager.addUpdate(entityId, update);
    }

    /**
     * Remove entities identified by removals from the engine
     * @threadsafe
     * @param removals Which entities to remove
     */
    public void addRemovals(Collection<Integer> removals) {
        this.entityManager.addRemovals(removals);
     }

    /**
     * Remove entity identified by removal from the engine
     * @threadsafe
     * @param removal Which entity to remove
     */
    public void addRemoval(Integer removal) {
        this.entityManager.addRemoval(removal);
    }

    /**
     * Tell the engine thread to quit
     * @threadsafe
     */
    public void quit() {
        Logger.getLogger(PhysicsEngine.class).info("Turning off physics engine...");
        this.interrupt();
    }

    /**
     * If the engine has started
     * @threadsafe
     * @return True if the engine is currently running. False otherwise
     */
    public boolean hasStarted() {
        return this.started.get();
    }

    /**
     * If the engine has quit.
     * @threadsafe
     * @return True if the engine has started and then quit.
     */
    public boolean hasQuit() {
        return this.quit;
    }

    @Override
    @Deprecated
    public void destroy() {
    }
}
