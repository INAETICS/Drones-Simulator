package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules;

import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.pubsub.api.pubsub.Publisher;

import java.util.Collections;
import java.util.List;

/**
 * Rule to send all messages received to the architecture through the Publisher
 */
public class SendMessages extends Rule {

    public SendMessages(Publisher publisher, IdentifierMapper id_mapper) {
        this.publisher = publisher;
        this.id_mapper = id_mapper;
    }

    /**
     * Create the logger
     */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SendMessages.class);

    /** Reference to the Publisher bundle */
    private final Publisher publisher;
    /** Reference to the Identifier Mapper bundle */
    private final IdentifierMapper id_mapper;

    /**
     * Send a protocol message through the publisher with its default topic
     * @param msg The message to send
     */
    private void sendProtocolMessage(ProtocolMessage msg) {
        publisher.send(msg);
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
