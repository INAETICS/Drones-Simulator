package org.inaetics.dronessimulator.common.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GameFinishedMessage extends ProtocolMessage {
    public GameFinishedMessage() {
        winner = null;
    }

    public GameFinishedMessage(String winner) {
        this.winner = winner;
    }

    private final String winner;

    public String getWinner() {
        return winner;
    }

    @Override
    public List<MessageTopic> getTopics() {
        List<MessageTopic> res = new ArrayList<>();
        res.add(MessageTopic.STATEUPDATES);
        return res;
        //return Collections.singletonList(MessageTopic.STATEUPDATES);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameFinishedMessage)) return false;
        GameFinishedMessage that = (GameFinishedMessage) o;
        return Objects.equals(winner, that.winner);
    }

    @Override
    public int hashCode() {

        return Objects.hash(winner);
    }
}
