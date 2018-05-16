package org.inaetics.dronessimulator.gameengine.common.gameevent;

import org.inaetics.dronessimulator.common.protocol.DamageMessage;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A game engine message which contains damage inflicted on a game entity.
 */
//@ToString(callSuper=true)
//@EqualsAndHashCode(callSuper=true)

public class DamageEvent extends GameEngineEvent {
    public DamageEvent(GameEntity entity, int dmg) {
        this.entity = entity;
        this.dmg = dmg;
    }

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

    public GameEntity getEntity() {
        return entity;
    }

    public int getDmg() {
        return dmg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DamageEvent)) return false;
        DamageEvent that = (DamageEvent) o;
        return dmg == that.dmg &&
                Objects.equals(entity, that.entity);
    }

    @Override
    public int hashCode() {

        return Objects.hash(entity, dmg);
    }

    @Override
    public String toString() {
        return "DamageEvent{" +
                "entity=" + entity +
                ", dmg=" + dmg +
                '}';
    }
}
