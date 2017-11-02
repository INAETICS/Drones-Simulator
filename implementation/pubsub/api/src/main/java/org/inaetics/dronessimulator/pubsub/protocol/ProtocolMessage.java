package org.inaetics.dronessimulator.pubsub.protocol;

import java.util.List;

/**
 * Abstract class for Drone Simulator messages.
 */
public abstract class ProtocolMessage implements Message {
    /**
     * First create an instance, then use setters.
     */
    public ProtocolMessage() {}

    public abstract List<MessageTopic> getTopics();
}
