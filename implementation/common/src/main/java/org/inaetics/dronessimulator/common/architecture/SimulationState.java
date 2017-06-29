package org.inaetics.dronessimulator.common.architecture;

/**
 * A state the architecture can take
 */
public enum SimulationState {
    NOSTATE ("nostate"),
    INIT ("init"),
    CONFIG ("config"),
    RUNNING ("running"),
    DONE ("done"),
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
