package org.inaetics.dronessimulator.physicsenginewrapper.physicsenginemessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.inaetics.dronessimulator.common.D3PoolCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.physicsengine.Entity;
import org.inaetics.dronessimulator.physicsenginewrapper.state.PhysicsEngineEntity;
import org.inaetics.dronessimulator.physicsenginewrapper.state.PhysicsEngineStateManager;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class CurrentStateMessage extends PhysicsEngineMessage {
    List<Entity> currentState;

    @Override
    public List<ProtocolMessage> getProtocolMessage(PhysicsEngineStateManager stateManager) {
        List<ProtocolMessage> msgs = new ArrayList<>();

        for(Entity e : currentState) {
            StateMessage msg = new StateMessage();
            PhysicsEngineEntity pee = stateManager.getById(e.getId());

            msg.setPosition(pee.getPosition());
            msg.setDirection(new D3PoolCoordinate());
            msg.setVelocity(pee.getVelocity());
            msg.setAcceleration(pee.getAcceleration());

            msgs.add(msg);
        }

        return msgs;
    }
}
