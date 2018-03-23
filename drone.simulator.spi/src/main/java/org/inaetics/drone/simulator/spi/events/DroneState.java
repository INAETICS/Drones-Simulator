package org.inaetics.drone.simulator.spi.events;

import org.inaetics.drone.simulator.common.D3Vector;

public class DroneState {
    private String teamName = "";
    private double timeValidity = 0.0;
    private D3Vector position = D3Vector.ZERO;
    private D3Vector velocity = D3Vector.ZERO;
    //TODO add more

    public DroneState() {}
    public DroneState(String teamName, double timeValidity, D3Vector position, D3Vector velocity) {
        this.teamName = teamName;
        this.timeValidity = timeValidity;
        this.position = position;
        this.velocity = velocity;
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
