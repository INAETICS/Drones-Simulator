package org.inaetics.dronessimulator.physicsenginewrapper.physicsenginemessage;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.inaetics.dronessimulator.common.protocol.CollisionMessage;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.physicsengine.Entity;
import org.inaetics.dronessimulator.physicsenginewrapper.state.PhysicsEngineStateManager;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Getter
public class CollisionStartMessage extends PhysicsEngineMessage {
    private final Entity e1;
    private final Entity e2;

    @Override
    public List<ProtocolMessage> getProtocolMessage(PhysicsEngineStateManager stateManager) {
        CollisionMessage msg = new CollisionMessage();

        msg.setE1Id(this.e1.getId());
        msg.setE1Type(stateManager.getTypeFor(this.e1.getId()));

        msg.setE2Id(this.e2.getId());
        msg.setE2Type(stateManager.getTypeFor(this.e2.getId()));

        return Collections.singletonList(msg);
    }
}
