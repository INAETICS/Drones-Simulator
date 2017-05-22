package org.inaetics.dronessimulator.gameengine.common.gameevent;


import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IIdentifierMapper;

import java.util.List;

/**
 * Unified messages from physics engine in our own type. Whichever type of messages the engine uses (if any),
 * we can always map to PhysicsEngineMessage. Used to generalize the ruleprocessors.
 */
public abstract class GameEngineEvent {

    /**
     * Get the messages to be broadcasted to everyone based on this message
     * @return Which messages to broadcast to all listeners
     */
    public abstract List<ProtocolMessage> getProtocolMessage(IIdentifierMapper id_mapper);
}
