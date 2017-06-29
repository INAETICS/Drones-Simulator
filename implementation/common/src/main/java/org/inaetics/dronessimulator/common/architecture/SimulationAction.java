package org.inaetics.dronessimulator.common.architecture;

/**
 * An action to be taken by the architecture
 */
public enum SimulationAction {
    INIT ("init"),
    CONFIG ("config"),
    START ("start"),
    STOP ("stop"),
    PAUSE ("pause"),
    RESUME ("resume"),
    GAMEOVER ("gameover"),
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
