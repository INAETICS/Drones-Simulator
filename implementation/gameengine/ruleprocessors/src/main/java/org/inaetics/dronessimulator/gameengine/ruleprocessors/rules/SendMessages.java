package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Rule to send all messages received to the architecture through the Publisher
 */
@AllArgsConstructor
@Log4j
public class SendMessages extends Rule {
    /** Reference to the Publisher bundle */
    private final Publisher publisher;
    /** Reference to the Identifier Mapper bundle */
    private final IdentifierMapper id_mapper;

    /**
     * Send a protocol message through the publisher with its default topic
     * @param msg The message to send
     */
    private void sendProtocolMessage(ProtocolMessage msg) {
        for(MessageTopic topic : msg.getTopics()) {
            try {
                publisher.send(topic, msg);
            } catch(IOException e) {
                log.fatal("Could not broadcast a message from SendMessages ruleset.", e);
            }
        }
    }

    @Override
    public void configRule() {
        // Nothing to config
    }

    @Override
    public List<GameEngineEvent> process(GameEngineEvent msg) {
        // Send Game event
        msg.getProtocolMessage(id_mapper).forEach(this::sendProtocolMessage);

        return Collections.singletonList(msg);
    }
}
