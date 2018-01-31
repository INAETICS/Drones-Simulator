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
    private final String teamname;

    @Getter
    private final D3Vector targetLocation;

    //Test constructor
    public Drone(int id, String teamname, D3Vector position, D3Vector velocity, D3Vector acceleration,
                 D3PolarCoordinate direction, D3Vector targetLocation) {
        super(id, DRONE_MAX_HEALTH, position, velocity, acceleration, direction);
        this.teamname = teamname;
        this.targetLocation = targetLocation;
    }

    public Drone(int id, String teamname, int currentHP, D3Vector position, D3Vector velocity, D3Vector acceleration, D3PolarCoordinate direction, D3Vector targetLocation) {
        super(id, currentHP, position, velocity, acceleration, direction);
        this.teamname = teamname;
        this.targetLocation = targetLocation;
    }

    /**
     * @return Which type the game entity is in terms of the protocol.
     */
    @Override
    public EntityType getType() {
        return EntityType.DRONE;
    }

    @Override
    public synchronized Drone deepCopy() {
        return new Drone(this.getEntityId(), this.getTeamname(), this.getHp(), this.getPosition(), this.getVelocity()
                , this.getAcceleration(), this.getDirection(), this.getTargetLocation());
    }
}