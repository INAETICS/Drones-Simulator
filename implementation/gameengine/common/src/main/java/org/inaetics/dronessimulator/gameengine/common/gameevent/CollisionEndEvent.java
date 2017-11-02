package org.inaetics.dronessimulator.gameengine.common.gameevent;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.pubsub.protocol.ProtocolMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * A unified physics engine message which contains the end of a collision between two game entities.
 */
@AllArgsConstructor
@Getter
public class CollisionEndEvent extends GameEngineEvent {
    /** First entity in the collision. */
    private final GameEntity e1;

    /** Second entity in the collision. */
    private final GameEntity e2;

    @Override
    public List<ProtocolMessage> getProtocolMessage(IdentifierMapper id_mapper) {
        // Do not need to broadcast any collision end messages (yet)
        return new ArrayList<>();
    }
}
