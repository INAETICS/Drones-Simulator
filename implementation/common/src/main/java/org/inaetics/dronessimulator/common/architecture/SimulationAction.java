package org.inaetics.dronessimulator.common.architecture;


public enum SimulationAction {
    INIT ("init"),
    CONFIG ("config"),
    START ("start"),
    STOP ("stop"),
    PAUSE ("pause"),
    RESUME ("resume"),
    GAMEOVER ("gameover"),
    DESTROY("destroy");

    private String name;

    SimulationAction(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
