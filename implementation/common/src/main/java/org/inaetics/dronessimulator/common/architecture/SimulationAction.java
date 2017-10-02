package org.inaetics.dronessimulator.common.architecture;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * An action to be taken by the architecture
 */
@AllArgsConstructor
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
    @Getter
    private String name;
}
