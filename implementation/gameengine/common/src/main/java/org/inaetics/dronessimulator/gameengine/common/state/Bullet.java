package org.inaetics.dronessimulator.gameengine.common.state;

import lombok.EqualsAndHashCode;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.pubsub.protocol.EntityType;

/**
 * A bullet game entity.
 */
 @EqualsAndHashCode(callSuper=true)
public class Bullet extends GameEntity<Bullet> {
    /** How much damage this bullet will inflict upon impact. */
    private final int dmg;

    /** The game entity that fired the bullet. */
    private final GameEntity firedBy;

    /**
     * Construction of a bullet entity.
     * @param id The id of the bullet entity.
     * @param dmg The damage of the bullet upon impact.
     * @param firedBy The game entity that fired the bullet.
     * @param position The starting position of the bullet.
     * @param velocity The velocity of the bullet.
     * @param acceleration The acceleration of the bullet.
     * @param direction The direction of the bullet.
     */
    public Bullet(int id, int dmg, GameEntity firedBy, D3Vector position, D3Vector velocity, D3Vector acceleration, D3PolarCoordinate direction) {
        super(id, position, velocity, acceleration, direction);

        this.firedBy = firedBy;
        this.dmg = dmg;
    }

    /**
     * Return the type of the game entity in terms of the protocol.
     * @return The protocol type.
     */
    @Override
    public EntityType getType() {
        return EntityType.BULLET;
    }

    @Override
    public Bullet deepCopy() {
        return new Bullet(this.getEntityId(), this.getDmg(), this.getFiredBy(), this.getPosition(), this.getVelocity(), this.getAcceleration(), this.getDirection());
    }

    /**
     * Returns the damage this bullet can inflict.
     * @return The damage.
     */
    public int getDmg() {
        return dmg;
    }

    /**
     * Returns the game entity that fired this bullet.
     * @return The game entity that fired this bullet.
     */
    public GameEntity getFiredBy() {
        return this.firedBy;
    }
}
