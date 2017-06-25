package org.inaetics.dronessimulator.common.architecture;


public enum SimulationState {
    NOSTATE ("nostate"),
    CONFIG ("config"),
    GAMEOVER ("gameover"),
    RUNNING ("running"),
    STOPPED ("stopped"),
    PAUSED ("paused");

    private final String name;

    SimulationState(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
