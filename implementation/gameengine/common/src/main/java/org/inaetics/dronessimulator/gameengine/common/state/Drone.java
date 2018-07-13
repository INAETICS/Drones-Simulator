package org.inaetics.dronessimulator.gameengine.common.state;

import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;

import java.util.Objects;

/**
 * A Drone game entity.
 */
//@EqualsAndHashCode(callSuper = true)
public class Drone extends HealthGameEntity<Drone> {
    /**
     * The maximum health of a drone.
     */
    public static final int DRONE_MAX_HEALTH = 100;

    private final String teamname;

    public String getTeamname() {
        return teamname;
    }

    private final D3Vector targetLocation;

    public D3Vector getTargetLocation() {
        return targetLocation;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Drone)) return false;
        if (!super.equals(o)) return false;
        Drone drone = (Drone) o;
        return Objects.equals(teamname, drone.teamname) &&
                Objects.equals(targetLocation, drone.targetLocation);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), teamname, targetLocation);
    }
}