package org.inaetics.dronessimulator.common.architecture;


public enum SimulationState {
    NOSTATE ("nostate"),
    INIT ("init"),
    CONFIG ("config"),
    RUNNING ("running"),
    DONE ("done"),
    PAUSED ("paused");

    private final String name;

    SimulationState(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
