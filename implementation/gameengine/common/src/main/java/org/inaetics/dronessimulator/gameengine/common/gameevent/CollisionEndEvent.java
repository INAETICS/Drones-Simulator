package org.inaetics.dronessimulator.gameengine.common.gameevent;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class CollisionEndEvent extends GameEngineEvent {
    private final GameEntity e1;
    private final GameEntity e2;

    @Override
    public List<ProtocolMessage> getProtocolMessage(IdentifierMapper id_mapper) {
        // Do not need to broadcast any collision end messages (yet)
        return new ArrayList<>();
    }
}
