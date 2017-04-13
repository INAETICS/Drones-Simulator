package org.inaetics.dronessimulator.physicsengine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.inaetics.dronessimulator.common.D3Vector;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Entity implements Cloneable {
    // X == Width, y == depth, z == height
    private final int id;
    private final Size size;
    private final boolean collideable;
    private D3Vector position; // m
    private D3Vector velocity; // m/s
    private D3Vector acceleration; // m/s^2

    public Entity(int id, Size size, boolean collideable) {
        this(id, size, collideable, new D3Vector(), new D3Vector(), new D3Vector());
    }

    public Entity(int id, Size size, boolean collideable, double x, double y, double z) {
        this(id, size, collideable, new D3Vector(x, y, z), new D3Vector(), new D3Vector());
    }

    public Entity(int id, Size size, boolean collideable, D3Vector position) {
        this(id, size, collideable, position, new D3Vector(), new D3Vector());
    }

    public Entity(int id, Size size, boolean collideable, D3Vector position, D3Vector velocity) {
        this(id, size, collideable, position, velocity, new D3Vector());
    }

    public void move(double time_in_seconds) {
        this.velocity = this.nextVelocity(this.acceleration, time_in_seconds);
        this.position = this.nextPosition(this.velocity, time_in_seconds);
    }

    public D3Vector nextVelocity(D3Vector nextAcceleration, double time_in_seconds) {
        return nextAcceleration.scale(time_in_seconds).add(this.velocity);
    }

    public D3Vector nextPosition(D3Vector nextVelocity, double time_in_seconds) {
        return nextVelocity.scale(time_in_seconds).add(this.position);
    }

    public boolean collides(Entity other) {
        return (this.getMinX() <= other.getMaxX() && this.getMaxX() >= other.getMinX()) &&
                (this.getMinY() <= other.getMaxY() && this.getMaxY() >= other.getMinY()) &&
                (this.getMinZ() <= other.getMaxZ() && this.getMaxZ() >= other.getMinZ());
    }

    public double getMinX() {
        return this.position.getX() - 0.5 * this.size.getWidth();
    }

    public double getMinY() {
        return this.position.getY() - 0.5 * this.size.getDepth();
    }

    public double getMinZ() {
        return this.position.getZ() - 0.5 * this.size.getHeight();
    }

    public double getMaxX() {
        return this.position.getX() + 0.5 * this.size.getWidth();
    }

    public double getMaxY() {
        return this.position.getY() + 0.5 * this.size.getDepth();
    }

    public double getMaxZ() {
        return this.position.getZ() + 0.5 * this.size.getHeight();
    }

    public boolean equals(Entity other) {
        return this.getId() == other.getId();
    }


    public static Entity copy(Entity entity) {
        return new Entity(entity.getId(), entity.getSize(), entity.isCollideable(), entity.getPosition(), entity.getVelocity(), entity.getAcceleration());
    }
}
