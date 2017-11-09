package org.inaetics.dronessimulator.gameengine.common.gameevent;

import lombok.RequiredArgsConstructor;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.protocol.GameFinishedMessage;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.common.protocol.RequestArchitectureStateChangeMessage;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;

import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
public class GameFinishedEvent extends GameEngineEvent {
    private final String winner;

    @Override
    public List<ProtocolMessage> getProtocolMessage(IdentifierMapper id_mapper) {
        List<ProtocolMessage> messages = new LinkedList<>();
        messages.add(new GameFinishedMessage(winner));
        //Also tell the architecture manager that the game is over
        RequestArchitectureStateChangeMessage requestArchitectureStateChangeMessage = new RequestArchitectureStateChangeMessage();
        requestArchitectureStateChangeMessage.setAction(SimulationAction.GAMEOVER);
        messages.add(requestArchitectureStateChangeMessage);

        return messages;
    }
}
