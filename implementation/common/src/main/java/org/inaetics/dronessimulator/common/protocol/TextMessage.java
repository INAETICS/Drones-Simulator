package org.inaetics.dronessimulator.common.protocol;


import java.util.ArrayList;
import java.util.List;

/**
 * Message used by radio component for text communication between components.
 */
public class TextMessage extends ProtocolMessage {
    /** the actual message */
    private String text = null;

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public List<MessageTopic> getTopics() {
        List<MessageTopic> topics = new ArrayList<>();

        topics.add(MessageTopic.RADIO);

        return topics;
    }
}
