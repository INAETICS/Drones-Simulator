package org.inaetics.dronessimulator.gameengine.common.state;

import lombok.EqualsAndHashCode;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.EntityType;

/**
 * A bullet game entity
 */
 @EqualsAndHashCode(callSuper=true)
public class Bullet extends GameEntity<Bullet> {
    /**
     * How much damage this bullet will do upon impact
     */
    private final int dmg;

    private final GameEntity firedBy;

    /**
     * Construction of a bullet entity
     * @param id The id of the bullet entity
     * @param dmg The damage of the bullet upon impact
     * @param position The starting position of the bullet
     * @param velocity The velocity of the bullet
     * @param acceleration The acceleration of the bullet
     */
    public Bullet(int id, int dmg, GameEntity firedBy, D3Vector position, D3Vector velocity, D3Vector acceleration) {
        super(id, position, velocity, acceleration);

        this.firedBy = firedBy;
        this.dmg = dmg;
    }

    /**
     * Return the protocol entity type of the game entity
     * @return Which type the game entity is in terms of the protocol
     */
    @Override
    public EntityType getType() {
        return EntityType.BULLET;
    }

    @Override
    public Bullet deepCopy() {
        return new Bullet(this.getEntityId(), this.getDmg(), this.getFiredBy(), this.getPosition(), this.getVelocity(), this.getAcceleration());
    }

    public int getDmg() {
        return dmg;
    }

    public GameEntity getFiredBy() {
        return this.firedBy;
    }
}
