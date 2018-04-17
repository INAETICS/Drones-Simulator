package org.inaetics.dronessimulator.common.protocol;

import java.util.Collections;
import java.util.List;

public class GameFinishedMessage extends ProtocolMessage {

    public GameFinishedMessage(String winner) {
        this.winner = winner;
    }

    private final String winner;

    public String getWinner() {
        return winner;
    }

    @Override
    public List<MessageTopic> getTopics() {
        return Collections.singletonList(MessageTopic.STATEUPDATES);
    }


}
