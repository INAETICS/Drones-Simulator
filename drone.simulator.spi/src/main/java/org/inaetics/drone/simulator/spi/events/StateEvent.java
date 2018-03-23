package org.inaetics.drone.simulator.spi.events;

import org.inaetics.drone.simulator.common.D3Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Every TBD (10ms) a update of the drones state/location is send by the game engine to
 * all subscriber on the "state-update" topic.
 * All drone states are combined in a single message to prevent hammering the network
 */
//NOTE default constructor and mutuaable properties to ensure (json) serialization works out of the box
public class StateEvent {

    private double timeValidity = 0.0;
    private List<DroneState> states = new ArrayList<>();

    public StateEvent() {}

    public StateEvent(List<DroneState> states) {
        this.states = states;
    }

    public List<DroneState> getStates() {
        return states;
    }

    public void setStates(List<DroneState> states) {
        this.states = states;
    }

    public double getTimeValidity() {
        return timeValidity;
    }

    public void setTimeValidity(double timeValidity) {
        this.timeValidity = timeValidity;
    }
}