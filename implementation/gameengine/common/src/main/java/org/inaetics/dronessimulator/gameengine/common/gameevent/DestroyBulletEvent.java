package org.inaetics.dronessimulator.gameengine.common.gameevent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.pubsub.protocol.EntityType;
import org.inaetics.dronessimulator.pubsub.protocol.KillMessage;
import org.inaetics.dronessimulator.pubsub.protocol.ProtocolMessage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A game engine message which signals the end of a bullet's life time.
 */
@AllArgsConstructor
@Getter
@ToString
public class DestroyBulletEvent extends DestroyEvent {
    /** The id of the bullet. */
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
