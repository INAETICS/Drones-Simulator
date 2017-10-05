package org.inaetics.dronessimulator.gameengine.common.gameevent;

import lombok.RequiredArgsConstructor;
import org.inaetics.dronessimulator.common.protocol.GameFinishedMessage;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class GameFinishedEvent extends GameEngineEvent {
    private final String winner;

    @Override
    public List<ProtocolMessage> getProtocolMessage(IdentifierMapper id_mapper) {
        return Collections.singletonList(new GameFinishedMessage(winner));
    }
}
