package org.inaetics.dronessimulator.physicsenginewrapper.physicsenginemessage;


import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.state.PhysicsEngineStateManager;

import java.util.List;

public abstract class PhysicsEngineMessage {

    public abstract List<ProtocolMessage> getProtocolMessage(PhysicsEngineStateManager stateManager);
}
