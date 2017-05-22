package org.inaetics.dronessimulator.gameengine.common.gameevent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.inaetics.dronessimulator.common.D3PoolCoordinate;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IIdentifierMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * A unified physicsengine message which contains the state of all entities
 */
@AllArgsConstructor
@Getter
public class CurrentStateEvent extends GameEngineEvent {
    /**
     * All entities as currently in the
     */
    List<GameEntity> currentState;

    @Override
    public List<ProtocolMessage> getProtocolMessage(IIdentifierMapper id_mapper) {
        List<ProtocolMessage> msgs = new ArrayList<>();

        for(GameEntity e : currentState) {
            StateMessage msg = new StateMessage();

            msg.setIdentifier(id_mapper.fromGameEngineToProtocolId(e.getEntityId()));
            msg.setType(e.getType());
            msg.setPosition(e.getPosition());
            msg.setDirection(new D3PoolCoordinate());
            msg.setVelocity(e.getVelocity());
            msg.setAcceleration(e.getAcceleration());

            msgs.add(msg);
        }

        return msgs;
    }
}
