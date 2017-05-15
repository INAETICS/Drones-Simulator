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

/**
 * An entity represented in the simulated world.
 */
public class Entity implements Cloneable {
    // X == Width, y == depth, z == height

    /**
     * The identifier of this entity
     */
    private final int id;
    /**
     * The size of the non-rotating hitbox around the entity
     */
    private final Size size;

    /**
     * The position of the entity in the world. In meters.
     */
    private D3Vector position; // m

    /**
     * The velocity of the entity in the world. In meters/seconds.
     */
    private D3Vector velocity; // m/s
    /**
     * The acceleration of the entity in the world. In meters/seconds^2.
     */
    private D3Vector acceleration; // m/s^2

    /**
     * Create an entity
     * @param id The id of the new entity
     * @param size The size of the non-rotating hitbox of the entity
     */
    public Entity(int id, Size size) {
        this(id, size, new D3Vector(), new D3Vector(), new D3Vector());
    }

    /**
     * Create an entity
     * @param id The id of the new entity
     * @param size The size of the non-rotating hitbox of the entity
     * @param x The position of the new entity on the x-axis in the world
     * @param y The position of the new entity on the y-axis in the world
     * @param z The position of the new entity on the z-axis in the world
     */
    public Entity(int id, Size size, double x, double y, double z) {
        this(id, size, new D3Vector(x, y, z), new D3Vector(), new D3Vector());
    }

    /**
     * Create an entity
     * @param id The id of the new entity
     * @param size The size of the non-rotating hitbox of the entity
     * @param position The position of the new entity in the world
     */
    public Entity(int id, Size size, D3Vector position) {
        this(id, size, position, new D3Vector(), new D3Vector());
    }

    /**
     * Create an entity
     * @param id The id of the new entity
     * @param size The size of the non-rotating hitbox of the entity
     * @param position The position of the new entity in the world
     * @param velocity The velocity of the new entity in the world
     */
    public Entity(int id, Size size, D3Vector position, D3Vector velocity) {
        this(id, size, position, velocity, new D3Vector());
    }

    /**
     * Move the entity in the world using the set velocity and acceleration for the timestep.
     * @param time_in_seconds How long the timestep is in seconds
     */
    public void move(double time_in_seconds) {
        this.velocity = this.nextVelocity(this.acceleration, time_in_seconds);
        this.position = this.nextPosition(this.velocity, time_in_seconds);
    }

    /**
     * What would the next velocity be of this entity using nextAcceleration for time_in_seconds long.
     * @param nextAcceleration The change in velocity for this timestep
     * @param time_in_seconds How long the timestep is in seconds
     * @return The next velocity for this entity
     */
    public D3Vector nextVelocity(D3Vector nextAcceleration, double time_in_seconds) {
        return nextAcceleration.scale(time_in_seconds).add(this.velocity);
    }

    /**
     * What would the next position be of this entity using nextVelocity for time_in_seconds long.
     * @param nextVelocity The change in position used for this timestep
     * @param time_in_seconds How long the timestep is in seconds
     * @return The next position for this entity
     */
    public D3Vector nextPosition(D3Vector nextVelocity, double time_in_seconds) {
        return nextVelocity.scale(time_in_seconds).add(this.position);
    }

    /**
     * If this and other Entity are colliding using their current position and Size.
     * @param other The other entity
     * @return If this and other entity are colliding
     */
    public boolean collides(Entity other) {
        return (this.getMinX() <= other.getMaxX() && this.getMaxX() >= other.getMinX()) &&
                (this.getMinY() <= other.getMaxY() && this.getMaxY() >= other.getMinY()) &&
                (this.getMinZ() <= other.getMaxZ() && this.getMaxZ() >= other.getMinZ());
    }

    /**
     * The minimal x value this entity occupies determined by position and size
     * @return The minimal x value this entity occupies determined by position and size
     */
    public double getMinX() {
        return this.position.getX() - 0.5 * this.size.getWidth();
    }

    /**
     * The minimal y value this entity occupies determined by position and size
     * @return The minimal y value this entity occupies determined by position and size
     */
    public double getMinY() {
        return this.position.getY() - 0.5 * this.size.getDepth();
    }

    /**
     * The minimal z value this entity occupies determined by position and size
     * @return The minimal z value this entity occupies determined by position and size
     */
    public double getMinZ() {
        return this.position.getZ() - 0.5 * this.size.getHeight();
    }

    /**
     * The maximum x value this entity occupies determined by position and size
     * @return The maximum x value this entity occupies determined by position and size
     */
    public double getMaxX() {
        return this.position.getX() + 0.5 * this.size.getWidth();
    }

    /**
     * The maximum y value this entity occupies determined by position and size
     * @return The maximum y value this entity occupies determined by position and size
     */
    public double getMaxY() {
        return this.position.getY() + 0.5 * this.size.getDepth();
    }

    /**
     * The maximum z value this entity occupies determined by position and size
     * @return The maximum z value this entity occupies determined by position and size
     */
    public double getMaxZ() {
        return this.position.getZ() + 0.5 * this.size.getHeight();
    }

    /**
     * If this and other Entity are the same Entity.
     * @param other The other entity
     * @return true if these are the same entities. False otherwise
     */
    public boolean equals(Entity other) {
        return this.getId() == other.getId();
    }

    /**
     * Deepcopy of the Entity
     * @param entity The entity to copy
     * @return A copy of the entity
     */
    public static Entity deepcopy(Entity entity) {
        return new Entity(entity.getId(), entity.getSize(), entity.getPosition(), entity.getVelocity(), entity.getAcceleration());
    }
}
