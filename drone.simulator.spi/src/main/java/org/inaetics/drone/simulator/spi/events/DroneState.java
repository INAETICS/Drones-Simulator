package org.inaetics.drone.simulator.spi.events;

import org.inaetics.drone.simulator.common.D3Vector;

import java.util.UUID;

public class DroneState {
    private double timeValidity;
    private UUID droneId;
    private String teamName;
    private D3Vector position;
    private D3Vector velocity;
    //TODO add more

    public DroneState() {}
    public DroneState(UUID droneId, String teamName, double timeValidity, D3Vector position, D3Vector velocity) {
        this.droneId = droneId;
        this.teamName = teamName;
        this.timeValidity = timeValidity;
        this.position = position;
        this.velocity = velocity;
    }

    public UUID getDroneId() {
        return droneId;
    }

    public void setDroneId(UUID droneId) {
        this.droneId = droneId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public double getTimeValidity() {
        return timeValidity;
    }

    public void setTimeValidity(double timeValidity) {
        this.timeValidity = timeValidity;
    }

    public D3Vector getPosition() {
        return position;
    }

    public void setPosition(D3Vector position) {
        this.position = position;
    }

    public D3Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(D3Vector velocity) {
        this.velocity = velocity;
    }
}
