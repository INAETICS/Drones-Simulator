package org.inaetics.dronessimulator.gameengine.common.gameevent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
@ToString
public class DestroyBulletEvent extends GameEngineEvent {
    private final int id;

    @Override
    public List<ProtocolMessage> getProtocolMessage(IdentifierMapper id_mapper) {
        Optional<String> maybeProtocolId = id_mapper.fromGameEngineToProtocolId(id);

        if(maybeProtocolId.isPresent()) {
            KillMessage msg = new KillMessage();

            msg.setIdentifier(maybeProtocolId.get());
            msg.setEntityType(EntityType.BULLET);

            return Collections.singletonList(msg);
        } else {
            return Collections.emptyList();
        }
    }
}
