package org.inaetics.dronessimulator.common.protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Message used by radio component for text communication between components.
 */
public class TextMessage extends ProtocolMessage {
    public TextMessage() {
    }

    public TextMessage(String text) {
        this.text = text;
    }

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
