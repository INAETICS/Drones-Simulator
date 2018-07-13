package org.inaetics.drone.simulator.spi.events;

import java.util.List;
import java.util.UUID;

public class DroneAnnouncement {
    private double timeValidity;
    private UUID droneId;
    private String droneTeamName;
    private double droneCost;
    private List<String> droneComponents;

    public DroneAnnouncement(double timeValidity, UUID droneId, String droneTeamName, double droneCost, List<String> droneComponents) {
        this.timeValidity = timeValidity;
        this.droneId = droneId;
        this.droneTeamName = droneTeamName;
        this.droneCost = droneCost;
        this.droneComponents = droneComponents;
    }

    public double getTimeValidity() {
        return timeValidity;
    }

    public void setTimeValidity(double timeValidity) {
        this.timeValidity = timeValidity;
    }

    public UUID getDroneId() {
        return droneId;
    }

    public void setDroneId(UUID droneId) {
        this.droneId = droneId;
    }

    public double getDroneCost() {
        return droneCost;
    }

    public void setDroneCost(double droneCost) {
        this.droneCost = droneCost;
    }

    public List<String> getDroneComponents() {
        return droneComponents;
    }

    public void setDroneComponents(List<String> droneComponents) {
        this.droneComponents = droneComponents;
    }
}

