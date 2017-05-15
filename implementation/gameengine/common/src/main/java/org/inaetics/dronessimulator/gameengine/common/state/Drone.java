package org.inaetics.dronessimulator.gameengine.common.state;

import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.EntityType;

/**
 * A Drone game entity
 */
public class Drone extends GameEntity<Drone> {
    /**
     * How much hp this drone has left
     */
    private volatile int hp;

    /**
     * Construction of a drone
     * @param id Id of the new drone
     * @param hp Max hp of the new drone
     * @param position Position of the new drone
     * @param velocity Velocity of the new drone
     * @param acceleration Acceleration of the new drone
     */
    public Drone(int id, int hp, D3Vector position, D3Vector velocity, D3Vector acceleration) {
        super(id, position, velocity, acceleration);

        this.hp = hp;
    }

    /**
     * Damage this drone with dmg damage.
     * @param dmg Amount of damage to the drone
     */
    public synchronized void damage(int dmg) {
        this.hp -= dmg;
    }

    /**
     * Return the protocol entity type of the game entity
     * @return Which type the game entity is in terms of the protocol
     */
    @Override
    public EntityType getType() {
        return EntityType.BULLET;
    }

    public synchronized int getHp() {
        return hp;
    }

    public synchronized void setHp(int hp) {
        this.hp = hp;
    }

    public synchronized Drone deepCopy() {
        return new Drone(this.getEntityId(), this.hp, this.getPosition(), this.getVelocity(), this.getAcceleration());
    }
}