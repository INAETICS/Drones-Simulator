package org.inaetics.dronessimulator.physicsenginewrapper.physicsenginemessage;


import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.state.GameStateManager;

import java.util.List;

/**
 * Unified messages from physics engine in our own type. Whichever type of messages the engine uses (if any),
 * we can always map to PhysicsEngineMessage. Used to generalize the ruleprocessors.
 */
public abstract class PhysicsEngineMessage {

    /**
     * Get the messages to be broadcasted to everyone based on this message
     * @param stateManager The game state manager
     * @return Which messages to broadcast to all listeners
     */
    public abstract List<ProtocolMessage> getProtocolMessage(GameStateManager stateManager);
}
