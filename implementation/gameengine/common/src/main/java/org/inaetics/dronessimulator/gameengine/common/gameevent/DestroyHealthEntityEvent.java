package org.inaetics.dronessimulator.gameengine.common.gameevent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.gameengine.common.state.HealthGameEntity;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IIdentifierMapper;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class DestroyHealthEntityEvent extends GameEngineEvent {
    private final HealthGameEntity destroyedEntity;

    @Override
    public List<ProtocolMessage> getProtocolMessage(IIdentifierMapper id_mapper) {
        KillMessage msg = new KillMessage();

        msg.setIdentifier(id_mapper.fromGameEngineToProtocolId(this.destroyedEntity.getEntityId()));
        msg.setEntityType(this.destroyedEntity.getType());

        return Collections.singletonList(msg);
    }
}
