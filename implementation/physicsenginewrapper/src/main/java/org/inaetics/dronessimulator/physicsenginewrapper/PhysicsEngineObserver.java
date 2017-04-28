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

@Getter
public class PhysicsEngineObserver implements PhysicsEngineEventObserver {
    private final LinkedBlockingQueue<PhysicsEngineMessage> outgoingQueue;

    public PhysicsEngineObserver() {
        this.outgoingQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void collisionStartHandler(Entity e1, Entity e2) {
        System.out.println("COLLISION START");
        this.outgoingQueue.add(new CollisionStartMessage(e1, e2));
    }

    @Override
    public void collisionStopHandler(Entity e1, Entity e2) {
        System.out.println("COLLISION STOP");
        this.outgoingQueue.add(new CollisionEndMessage(e1, e2));
    }

    @Override
    public void broadcastStateHandler(List<Entity> currentState) {
        System.out.println("BROADCAST STATE");
        this.outgoingQueue.add(new CurrentStateMessage(currentState));
    }
}
