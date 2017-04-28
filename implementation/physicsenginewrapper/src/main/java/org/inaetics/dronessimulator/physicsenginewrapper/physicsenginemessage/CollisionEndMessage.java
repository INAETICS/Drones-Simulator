package org.inaetics.dronessimulator.physicsenginewrapper.physicsenginemessage;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.physicsengine.Entity;
import org.inaetics.dronessimulator.physicsenginewrapper.state.PhysicsEngineStateManager;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class CollisionEndMessage extends PhysicsEngineMessage {
    private final Entity e1;
    private final Entity e2;

    @Override
    public List<ProtocolMessage> getProtocolMessage(PhysicsEngineStateManager stateManager) {
        // Do not need to broadcast any collision end messages (yet)
        return new ArrayList<>();
    }
}
