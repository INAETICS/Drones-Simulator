package org.inaetics.dronessimulator.common.architecture;

/**
 * A state the architecture can take
 */
public enum SimulationState {
    /**
     * No state can be found currently
     */
    NOSTATE ("nostate"),
    /**
     * Simulation is in the initial state / lobby
     */
    INIT ("init"),
    /**
     * Simulation is configured
     */
    CONFIG ("config"),
    /**
     * Simulation is running
     */
    RUNNING ("running"),
    /**
     * Simulation is over
     */
    DONE ("done"),
    /**
     * Simulation is paused
     */
    PAUSED ("paused");

    /**
     * Human-readable name of state
     */
    private final String name;

    /**
     * Construct a state
     * @param name The human-readable name of the state
     */
    SimulationState(String name) {
        this.name = name;
    }

    /**
     * Get the human-readable name of the state
     * @return The humean-readable name
     */
    public String getName() {
        return this.name;
    }
}
