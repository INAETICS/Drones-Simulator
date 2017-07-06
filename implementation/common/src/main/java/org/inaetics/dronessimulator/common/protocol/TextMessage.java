package org.inaetics.dronessimulator.common.protocol;


import java.util.ArrayList;
import java.util.List;

public class TextMessage extends ProtocolMessage {
    /** Indentifier of object */
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
