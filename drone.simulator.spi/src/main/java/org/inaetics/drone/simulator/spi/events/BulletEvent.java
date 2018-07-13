package org.inaetics.drone.simulator.spi.events;

import org.inaetics.drone.simulator.common.D3Vector;

import java.util.UUID;

public class BulletEvent {
    private double timeValidity;
    private UUID owner;
    private D3Vector initialPos;
    private D3Vector velocity;

    public BulletEvent(double timeValidity, UUID owner, D3Vector initialPos, D3Vector velocity) {
        this.timeValidity = timeValidity;
        this.owner = owner;
        this.initialPos = initialPos;
        this.velocity = velocity;
    }

    public double getTimeValidity() {
        return timeValidity;
    }

    public void setTimeValidity(double timeValidity) {
        this.timeValidity = timeValidity;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public D3Vector getInitialPos() {
        return initialPos;
    }

    public void setInitialPos(D3Vector initialPos) {
        this.initialPos = initialPos;
    }

    public D3Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(D3Vector velocity) {
        this.velocity = velocity;
    }
}
