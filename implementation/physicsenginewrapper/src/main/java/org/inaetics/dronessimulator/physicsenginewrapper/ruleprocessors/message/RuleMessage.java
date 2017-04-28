package org.inaetics.dronessimulator.physicsenginewrapper.ruleprocessors.message;


import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.state.PhysicsEngineStateManager;

import java.util.List;

public abstract class RuleMessage {

    public abstract List<ProtocolMessage> getProtocolMessage(PhysicsEngineStateManager stateManager);
}
