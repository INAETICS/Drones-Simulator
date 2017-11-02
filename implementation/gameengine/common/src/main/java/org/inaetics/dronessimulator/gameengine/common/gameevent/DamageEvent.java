package org.inaetics.dronessimulator.gameengine.common.gameevent;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.pubsub.protocol.DamageMessage;
import org.inaetics.dronessimulator.pubsub.protocol.ProtocolMessage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A game engine message which contains damage inflicted on a game entity.
 */
@AllArgsConstructor
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
@Getter
public class DamageEvent extends GameEngineEvent {
    /** The game entity that is damaged. */
    private final GameEntity entity;

    /** The damage inflicted on the entity. */
    private final int dmg;

    @Override
    public List<ProtocolMessage> getProtocolMessage(IdentifierMapper id_mapper) {
        Optional<String> maybeProtocolId = id_mapper.fromGameEngineToProtocolId(this.entity.getEntityId());

        if(maybeProtocolId.isPresent()) {
            DamageMessage msg = new DamageMessage();

            msg.setEntityId(maybeProtocolId.get());
            msg.setEntityType(this.entity.getType());
            msg.setDamage(this.dmg);

            return Collections.singletonList(msg);
        } else {
            return Collections.emptyList();
        }
    }
}
