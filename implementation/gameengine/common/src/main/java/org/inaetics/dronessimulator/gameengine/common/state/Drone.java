package org.inaetics.dronessimulator.gameengine.common.state;

import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.EntityType;

/**
 * A Drone game entity
 */
public class Drone extends HealthGameEntity<Drone> {
    public static final int DRONE_MAX_HEALTH = 100;

    /**
     * Construction of a drone
     * @param id Id of the new drone
     * @param position Position of the new drone
     * @param velocity Velocity of the new drone
     * @param acceleration Acceleration of the new drone
     */
    public Drone(int id, D3Vector position, D3Vector velocity, D3Vector acceleration) {
        super(id, DRONE_MAX_HEALTH, position, velocity, acceleration);
    }

    /**
     * Construction of a drone
     * @param id Id of the new drone
     * @param currentHP Max hp of the new drone
     * @param position Position of the new drone
     * @param velocity Velocity of the new drone
     * @param acceleration Acceleration of the new drone
     */
    public Drone(int id, int currentHP, D3Vector position, D3Vector velocity, D3Vector acceleration) {
        super(id, currentHP, position, velocity, acceleration);
    }

    /**
     * Return the protocol entity type of the game entity
     * @return Which type the game entity is in terms of the protocol
     */
    @Override
    public EntityType getType() {
        return EntityType.BULLET;
    }

    public synchronized Drone deepCopy() {
        return new Drone(this.getEntityId(), this.getHP(), this.getPosition(), this.getVelocity(), this.getAcceleration());
    }
}