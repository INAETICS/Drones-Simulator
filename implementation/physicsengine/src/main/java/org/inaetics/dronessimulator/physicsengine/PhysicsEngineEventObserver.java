package org.inaetics.dronessimulator.physicsengine;

import java.util.List;

public interface PhysicsEngineEventObserver {

    public void collisionStartHandler(Entity e1, Entity e2);
    public void collisionStopHandler(Entity e1, Entity e2);
    public void broadcastStateHandler(List<Entity> currentState);
}
