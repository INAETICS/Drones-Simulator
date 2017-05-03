package org.inaetics.dronessimulator.gameengine.common.gameevent;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.inaetics.dronessimulator.common.protocol.CollisionMessage;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Getter
public class CollisionStartEvent extends GameEngineEvent {
    private final GameEntity e1;
    private final GameEntity e2;

    @Override
    public List<ProtocolMessage> getProtocolMessage() {
        CollisionMessage msg = new CollisionMessage();

        msg.setE1Id(this.e1.getEntityId());
        msg.setE1Type(this.e1.getType());

        msg.setE2Id(this.e2.getEntityId());
        msg.setE2Type(this.e2.getType());

        return Collections.singletonList(msg);
    }
}
