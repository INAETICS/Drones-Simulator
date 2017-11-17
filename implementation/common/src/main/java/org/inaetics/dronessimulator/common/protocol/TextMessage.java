package org.inaetics.dronessimulator.common.protocol;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Message used by radio component for text communication between components.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
