package org.inaetics.dronessimulator.physicsenginewrapper.ruleprocessors.rules;

import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.physicsenginemessage.PhysicsEngineMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.ruleprocessors.message.RuleMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.state.PhysicsEngineStateManager;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
public class SendMessages extends Processor {
    private final Publisher publisher;

    private void sendProtocolMessage(ProtocolMessage msg) {
        //System.out.println("Sending message over topics " + msg.getTopics());
        for(MessageTopic topic : msg.getTopics()) {
            try {
                //System.out.println("Sending message. Topic: " + topic + ", msg: " + msg);
                publisher.send(topic, msg);
            } catch(IOException e) {
                Logger.getLogger(SendMessages.class).fatal("Could not broadcast a message from SendMessages ruleset.", e);
            }
        }
    }

    @Override
    public void process(PhysicsEngineStateManager stateManager, PhysicsEngineMessage msg, List<RuleMessage> results) {
        // Send physics engine msg
        trace(msg.getProtocolMessage(stateManager)).forEach(this::sendProtocolMessage);
        // Send messages resulting from ruleset
        results.forEach((m) -> trace(m.getProtocolMessage(stateManager)).forEach(this::sendProtocolMessage));
    }

    public <E> E trace(E e) {
        System.out.println(e);
        return e;
    }
}
