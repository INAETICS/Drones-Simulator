package org.inaetics.dronessimulator.gameengine.common.gameevent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.gameengine.common.state.HealthGameEntity;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
@ToString
public class DestroyHealthEntityEvent extends GameEngineEvent {
    private final HealthGameEntity destroyedEntity;

    @Override
    public List<ProtocolMessage> getProtocolMessage(IdentifierMapper id_mapper) {
        Optional<String> maybeProtocolId = id_mapper.fromGameEngineToProtocolId(this.destroyedEntity.getEntityId());

        if(maybeProtocolId.isPresent()) {
            KillMessage msg = new KillMessage();

            msg.setIdentifier(maybeProtocolId.get());
            msg.setEntityType(this.destroyedEntity.getType());

            return Collections.singletonList(msg);
        } else {
            return Collections.emptyList();
        }
    }
}
