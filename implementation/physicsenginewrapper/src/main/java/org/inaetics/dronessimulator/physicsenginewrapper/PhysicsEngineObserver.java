package org.inaetics.dronessimulator.physicsenginewrapper;

import lombok.Getter;
import org.inaetics.dronessimulator.physicsengine.Entity;
import org.inaetics.dronessimulator.physicsengine.PhysicsEngineEventObserver;
import org.inaetics.dronessimulator.physicsenginewrapper.physicsenginemessage.CollisionEndMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.physicsenginemessage.CollisionStartMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.physicsenginemessage.CurrentStateMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.physicsenginemessage.PhysicsEngineMessage;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An observer for the {@link org.inaetics.dronessimulator.physicsengine.PhysicsEngine}.
 * Wraps any events into a {@link org.inaetics.dronessimulator.physicsenginewrapper.physicsenginemessage} message
 * and puts it into the outgoingQueue for further processing.
 */
@Getter
public class PhysicsEngineObserver implements PhysicsEngineEventObserver {
    private final LinkedBlockingQueue<PhysicsEngineMessage> outgoingQueue;

    /**
     * Create an observer and send all events to the outgoingQueue
     * @param outgoingQueue Send all events to this queue
     */
    public PhysicsEngineObserver(LinkedBlockingQueue<PhysicsEngineMessage> outgoingQueue) {
        this.outgoingQueue = outgoingQueue;
    }

    /**
     * How to handle a collision start event. Send to outgoingqueue.
     * @param e1 First entity in the collision
     * @param e2 Second entity in the collision
     */
    @Override
    public void collisionStartHandler(Entity e1, Entity e2) {
        this.outgoingQueue.add(new CollisionStartMessage(e1, e2));
    }

    /**
     * How to handle a collision stop event. Send to outgoingqueue.
     * @param e1 First entity in the ended collision
     * @param e2 Second entity in the ended collision
     */
    @Override
    public void collisionStopHandler(Entity e1, Entity e2) {
        this.outgoingQueue.add(new CollisionEndMessage(e1, e2));
    }

    /**
     * How to handle a broadcast state event. Send to outgoingqueue
     * @param currentState All information about all entities. Deepcopy so no link to state in physicsengine.
     */
    @Override
    public void broadcastStateHandler(List<Entity> currentState) {
        this.outgoingQueue.add(new CurrentStateMessage(currentState));
    }
}
