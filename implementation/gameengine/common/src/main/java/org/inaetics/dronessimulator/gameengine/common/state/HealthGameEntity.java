package org.inaetics.dronessimulator.gameengine.common.state;

import lombok.EqualsAndHashCode;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.EntityType;

@EqualsAndHashCode(callSuper=true)
public abstract class HealthGameEntity<C extends GameEntity<C>> extends GameEntity<C> {
    /**
     * How much hp this entity has left
     */
    private volatile int hp;

    public HealthGameEntity(int entityId, int hp, D3Vector position, D3Vector velocity, D3Vector acceleration) {
        super(entityId, position, velocity, acceleration);

        this.hp = hp;
    }

    @Override
    public abstract EntityType getType();

    @Override
    public abstract C deepCopy();


    public synchronized int getHP() {
        return hp;
    }

    /**
     * Damage this entity with dmg damage.
     * @param dmg Amount of damage to the entity
     */
    public synchronized void damage(int dmg) {
        this.hp -= dmg;
    }
}
