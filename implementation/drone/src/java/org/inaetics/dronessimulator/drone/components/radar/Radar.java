package org.inaetics.dronessimulator.drone.components.radar;

import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by mart on 15-5-17.
 */
public interface Radar {

    ConcurrentLinkedQueue<ProtocolMessage> getMessages();
}
