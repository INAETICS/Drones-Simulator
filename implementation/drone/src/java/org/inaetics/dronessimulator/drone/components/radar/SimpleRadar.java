package org.inaetics.dronessimulator.drone.components.radar;

import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.*;
import org.inaetics.dronessimulator.drone.components.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by mart on 15-5-17.
 */
public class SimpleRadar implements Component {
    private final ConcurrentLinkedQueue<ProtocolMessage> messages;
    private final ConcurrentHashMap<String, StateMessage> surrounding_drones;

    private static final int RADAR_RANGE = 100;

    public SimpleRadar(){
        this.messages = new ConcurrentLinkedQueue<ProtocolMessage>();
        this.surrounding_drones = new ConcurrentHashMap<String, D3Vector>();
    }

    public ConcurrentLinkedQueue<ProtocolMessage> getMessages(){
        return messages;
    }
}
