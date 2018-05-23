package org.inaetics.dronessimulator.drone.components.radio;

public class RadioMessage {
    private final String teamName;
    private final Object message;

    public RadioMessage(String teamName, Object message) {
        this.teamName = teamName;
        this.message = message;
    }

    /**
     * Empty constructor for serialization
     */
    public RadioMessage() {
        this.teamName = null;
        this.message = null;
    }

    public String getTeamName() {
        return teamName;
    }

    public Object getMessage() {
        return message;
    }
}
