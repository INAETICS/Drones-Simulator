package org.inaetics.dronessimulator.gameengine.common.gameevent;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class DestroyDroneEvent extends GameEngineEvent {
    private final int id;

    @Override
    public List<ProtocolMessage> getProtocolMessage() {
        KillMessage msg = new KillMessage();

        msg.setEntityId(this.id);
        msg.setEntityType(EntityType.DRONE);

        return Collections.singletonList(msg);
    }
}
