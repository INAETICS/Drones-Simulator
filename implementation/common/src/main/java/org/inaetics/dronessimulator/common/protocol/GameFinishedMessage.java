package org.inaetics.dronessimulator.common.protocol;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class GameFinishedMessage extends ProtocolMessage {
    @Getter
    private final String winner;

    @Override
    public List<MessageTopic> getTopics() {
        return Collections.singletonList(MessageTopic.STATEUPDATES);
    }
}
