package org.inaetics.dronessimulator.common.architecture;

/**
 * An action to be taken by the architecture
 */
public enum SimulationAction {
    /**
     * Initialize the architecture
     * Is taken only when the architecture transitions with (NOSTATE, INIT, INIT)
     * This only happens when the initial state is first published to storage
     */
    INIT ("init"),
    /**
     * Configure the architecture
     */
    CONFIG ("config"),
    /**
     * Start the simulation
     */
    START ("start"),
    /**
     * Stop the simulation
     */
    STOP ("stop"),
    /**
     * Pause the simulation
     */
    PAUSE ("pause"),
    /**
     * Resume the simulation after pause
     */
    RESUME ("resume"),
    /**
     * Wincondition! Simulation is done
     */
    GAMEOVER ("gameover"),
    /**
     * Destroy the architecture
     * Is called only when the published current state is removed from storage
     */
    DESTROY("destroy");

    /**
     * The name of the architecture Action
     */
    private String name;

    /**
     * Construct a SimulationAction
     * @param name The name of the action
     */
    SimulationAction(String name) {
        this.name = name;
    }

    /**
     * The human-readable name of the action
     * @return The human-readable name of the action
     */
    public String getName() {
        return this.name;
    }
}
