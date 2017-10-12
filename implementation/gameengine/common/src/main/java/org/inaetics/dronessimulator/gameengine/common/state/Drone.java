package org.inaetics.dronessimulator.gameengine.common.state;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;

/**
 * A Drone game entity.
 */
@EqualsAndHashCode(callSuper = true)
public class Drone extends HealthGameEntity<Drone> {
    /**
     * The maximum health of a drone.
     */
    public static final int DRONE_MAX_HEALTH = 100;
    @Getter
    private final String team;

    /**
     * Construction of a drone.
     *
     * @param id           Id of the new drone.
     * @param position     Position of the new drone.
     * @param velocity     Velocity of the new drone.
     * @param acceleration Acceleration of the new drone.
     * @param direction    Direction of the new drone.
     */
    public Drone(int id, String team, D3Vector position, D3Vector velocity, D3Vector acceleration, D3PolarCoordinate direction) {
        super(id, DRONE_MAX_HEALTH, position, velocity, acceleration, direction);
        this.team = team;
    }

    /**
     * Construction of a drone.
     *
     * @param id           Id of the new drone.
     * @param currentHP    Max hp of the new drone.
     * @param position     Position of the new drone.
     * @param velocity     Velocity of the new drone.
     * @param acceleration Acceleration of the new drone.
     * @param direction    Direction of the new drone.
     */
    public Drone(int id, String team, int currentHP, D3Vector position, D3Vector velocity, D3Vector acceleration, D3PolarCoordinate direction) {
        super(id, currentHP, position, velocity, acceleration, direction);
        this.team = team;

    }

    /**
     * Return the protocol entity type of the game entity.
     *
     * @return Which type the game entity is in terms of the protocol.
     */
    @Override
    public EntityType getType() {
        return EntityType.DRONE;
    }

    @Override
    public synchronized Drone deepCopy() {
        return new Drone(this.getEntityId(), this.getTeam(), this.getHp(), this.getPosition(), this.getVelocity(), this.getAcceleration(), this.getDirection());
    }
}