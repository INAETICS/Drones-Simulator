package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules;

import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IIdentifierMapper;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class SendMessages extends Processor {
    private final Publisher publisher;
    private final IIdentifierMapper id_mapper;

    private void sendProtocolMessage(ProtocolMessage msg) {
        for(MessageTopic topic : msg.getTopics()) {
            try {
                publisher.send(topic, msg);
            } catch(IOException e) {
                Logger.getLogger(SendMessages.class).fatal("Could not broadcast a message from SendMessages ruleset.", e);
            }
        }
    }

    @Override
    public List<GameEngineEvent> process(GameEngineEvent msg) {
        // Send Game event
        msg.getProtocolMessage(id_mapper).forEach(this::sendProtocolMessage);

        return Collections.singletonList(msg);
    }
}
