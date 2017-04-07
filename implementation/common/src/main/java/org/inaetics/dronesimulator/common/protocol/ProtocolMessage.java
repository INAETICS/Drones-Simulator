package org.inaetics.dronesimulator.common.protocol;

import org.inaetics.dronessimulator.pubsub.api.Message;

/**
 * Abstract class for Drone Simulator messages.
 */
public abstract class ProtocolMessage implements Message {
    /**
     * First create an instance, then use setters.
     */
    public ProtocolMessage() {}
}
