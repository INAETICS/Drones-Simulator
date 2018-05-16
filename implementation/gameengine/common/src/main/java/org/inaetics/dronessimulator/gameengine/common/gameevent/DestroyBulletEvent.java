package org.inaetics.dronessimulator.gameengine.common.gameevent;

import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A game engine message which signals the end of a bullet's life time.
 */
public class DestroyBulletEvent extends DestroyEvent {
    public DestroyBulletEvent(int id) {
        this.id = id;
    }

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

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "DestroyBulletEvent{" +
                "id=" + id +
                '}';
    }
}
