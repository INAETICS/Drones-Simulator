package org.inaetics.dronessimulator.common.architecture;


public enum SimulationAction {
    INIT ("init"),
    START ("start"),
    RESTART ("restart"),
    STOP ("stop"),
    PAUSE ("pause"),
    RESUME ("resume"),
    GAMEOVER ("gameover");

    private String name;

    SimulationAction(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
