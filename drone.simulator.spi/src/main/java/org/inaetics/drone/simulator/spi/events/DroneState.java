package org.inaetics.drone.simulator.spi.events;

import org.inaetics.drone.simulator.common.D3Vector;

public class DroneState {
    private final String teamName;
    private final double timeValidity;
    private final D3Vector position;
    private final D3Vector velocity;
    //TODO add more

    public DroneState(String teamName, double timeValidity, D3Vector position, D3Vector velocity) {
        this.teamName = teamName;
        this.timeValidity = timeValidity;
        this.position = position;
        this.velocity = velocity;
    }

    public String getTeamName() {
        return teamName;
    }

    public double getTimeValidity() {
        return timeValidity;
    }

    public D3Vector getPosition() {
        return position;
    }

    public D3Vector getVelocity() {
        return velocity;
    }

}
