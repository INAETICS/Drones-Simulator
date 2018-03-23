package org.inaetics.drone.simulator.spi.events;

public class GameEvent {

    public enum GameEventType {
        START,
        PAUSE,
        RESET
    }

    private double timeValidity;
    private GameEventType type;

    public GameEvent() {}
    public GameEvent(double timeValidity, GameEventType type) {
        this.timeValidity = timeValidity;
        this.type = type;
    }

    public double getTimeValidity() {
        return timeValidity;
    }

    public void setTimeValidity(double timeValidity) {
        this.timeValidity = timeValidity;
    }

    public GameEventType getType() {
        return type;
    }

    public void setType(GameEventType type) {
        this.type = type;
    }
}
