package org.inaetics.dronessimulator.pubsub.protocol;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Message used by radio component for text communication between components.
 */
@Getter
@Setter
public class TextMessage extends ProtocolMessage {
    /**
     * the actual message
     */
    private String text = null;

    @Override
    public List<MessageTopic> getTopics() {
        List<MessageTopic> topics = new ArrayList<>();

        topics.add(MessageTopic.RADIO);

        return topics;
    }
}
