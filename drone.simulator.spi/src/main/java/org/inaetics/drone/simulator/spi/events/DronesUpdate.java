package org.inaetics.drone.simulator.spi.events;

import org.inaetics.drone.simulator.common.D3Vector;

import java.util.List;

/**
 * Every TBD (10ms) a update of the drones state/location is send by the game engine to
 * all subscriber on the "drones-update" topic.
 * All drone states are combined in a single message to prevent hammering the network
 */
public class DronesUpdate {

    private final List<DroneState> states;

    public DronesUpdate(List<DroneState> states) {
        this.states = states;
    }

    public List<DroneState> getStates() {
        return states;
    }
}