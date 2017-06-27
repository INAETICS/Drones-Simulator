package org.inaetics.dronessimulator.physicsengine;

import java.util.List;

/**
 * Observer interface for the physics engine. Each method corresponds with an event.
 */
public interface PhysicsEngineEventObserver {
    /**
     * What to do when a collision starts. You will receive one event for a collision between entities a and b.
     * So there will not be a second event for collision between b and a.
     * @param e1 First entity in the collision.
     * @param e2 Second entity in the collision.
     */
    public void collisionStartHandler(Entity e1, Entity e2);

    /**
     * What to do when a collision stops. You will receive one event for the stop between entities a and b.
     * So there will not be a second event for the stop between b and a.
     * @param e1 First entity in the ended collision.
     * @param e2 Second entity in the ended collision.
     */
    public void collisionStopHandler(Entity e1, Entity e2);

    /**
     * What to do when a broadcast of the current state is send.
     * @param currentState All information about all entities. The received states are deep copies, so there are no
     *                     negative effects to the state inside the physics engine.
     */
    public void broadcastStateHandler(List<Entity> currentState);
}
