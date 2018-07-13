package org.inaetics.dronessimulator.gameengine.common.state;

import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;

import java.util.Objects;

/**
 * Base game entity with health points.
 */
//@EqualsAndHashCode(callSuper=true)
public abstract class HealthGameEntity<C extends GameEntity<C>> extends GameEntity<C> {
    /** The amount of hp this entity has left. */
    private volatile int hp;

    public int getHp() {
        return hp;
    }

    /**
     * Construction of a health game entity.
     * @param entityId Id of the new entity.
     * @param position Position of the new entity.
     * @param velocity Velocity of the new entity.
     * @param acceleration Acceleration of the new entity.
     * @param direction Direction of the new entity.
     */
    public HealthGameEntity(int entityId, int hp, D3Vector position, D3Vector velocity, D3Vector acceleration, D3PolarCoordinate direction) {
        super(entityId, null, position, velocity, acceleration, direction);

        this.hp = hp;
    }

    @Override
    public abstract EntityType getType();

    @Override
    public abstract C deepCopy();

    /**
     * Damage this entity with the given amount of damage.
     * @param dmg Amount of damage to inflict on the entity
     */
    public void damage(int dmg) {
        this.hp -= dmg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HealthGameEntity)) return false;
        if (!super.equals(o)) return false;
        HealthGameEntity<?> that = (HealthGameEntity<?>) o;
        return hp == that.hp;
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), hp);
    }
}
