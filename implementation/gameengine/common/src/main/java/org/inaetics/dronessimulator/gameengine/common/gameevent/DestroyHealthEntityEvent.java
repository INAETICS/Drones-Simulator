package org.inaetics.dronessimulator.gameengine.common.gameevent;

import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.gameengine.common.state.HealthGameEntity;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A game engine message which signals the end of the life of a game entity with health.
 */
public class DestroyHealthEntityEvent extends DestroyEvent {
    public DestroyHealthEntityEvent(HealthGameEntity destroyedEntity) {
        this.destroyedEntity = destroyedEntity;
    }

    /** The game entity that got destroyed. */
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

    public HealthGameEntity getDestroyedEntity() {
        return destroyedEntity;
    }

    @Override
    public String toString() {
        return "DestroyHealthEntityEvent{" +
                "destroyedEntity=" + destroyedEntity +
                '}';
    }
}
