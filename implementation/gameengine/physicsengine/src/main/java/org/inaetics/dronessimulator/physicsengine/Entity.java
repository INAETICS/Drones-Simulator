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
 * x == width, y == depth, z == height
 */
public class Entity implements Cloneable {

    /** The identifier of this entity. */
    private final int id;

    /** The size of the non-rotating hitbox around the entity. */
    private final Size size;

    /** The position of the entity in the world. In meters. */
    private D3Vector position; // m

    /** The velocity of the entity in the world. In meters/second. */
    private D3Vector velocity; // m/s

    /** The acceleration of the entity in the world. In meters/second^2. */
    private D3Vector acceleration; // m/s^2

    /**
     * Creates an entity.
     * @param id The id of the new entity.
     * @param size The size of the non-rotating hitbox of the entity.
     */
    public Entity(int id, Size size) {
        this(id, size, new D3Vector(), new D3Vector(), new D3Vector());
    }

    /**
     * Creates an entity.
     * @param id The id of the new entity.
     * @param size The size of the non-rotating hitbox of the entity.
     * @param x The position of the new entity on the x-axis in the world.
     * @param y The position of the new entity on the y-axis in the world.
     * @param z The position of the new entity on the z-axis in the world.
     */
    public Entity(int id, Size size, double x, double y, double z) {
        this(id, size, new D3Vector(x, y, z), new D3Vector(), new D3Vector());
    }

    /**
     * Creates an entity.
     * @param id The id of the new entity.
     * @param size The size of the non-rotating hitbox of the entity.
     * @param position The position of the new entity in the world.
     */
    public Entity(int id, Size size, D3Vector position) {
        this(id, size, position, new D3Vector(), new D3Vector());
    }

    /**
     * Creates an entity.
     * @param id The id of the new entity.
     * @param size The size of the non-rotating hitbox of the entity.
     * @param position The position of the new entity in the world.
     * @param velocity The velocity of the new entity in the world.
     */
    public Entity(int id, Size size, D3Vector position, D3Vector velocity) {
        this(id, size, position, velocity, new D3Vector());
    }

    /**
     * Moves the entity in the world using the set velocity and acceleration for the time step.
     * @param time_in_seconds The time step in seconds.
     */
    public void move(double time_in_seconds) {
        this.velocity = this.nextVelocity(this.acceleration, time_in_seconds);
        this.position = this.nextPosition(this.velocity, time_in_seconds);
    }

    /**
     * Calculates the velocity for this entity after the given period of the given acceleration.
     * @param nextAcceleration The change in velocity during time step.
     * @param time_in_seconds The time step is in seconds.
     * @return The next velocity for this entity.
     */
    public D3Vector nextVelocity(D3Vector nextAcceleration, double time_in_seconds) {
        return nextAcceleration.scale(time_in_seconds).add(this.velocity);
    }

    /**
     * Calculates the position of this entity after the given period of the given velocity.
     * @param nextVelocity The change in position during this time step.
     * @param time_in_seconds How long the time step is in seconds.
     * @return The next position for this entity.
     */
    public D3Vector nextPosition(D3Vector nextVelocity, double time_in_seconds) {
        return nextVelocity.scale(time_in_seconds).add(this.position);
    }

    /**
     * Test whether this entity and the given entity are colliding at their current position.
     * @param other The other entity.
     * @return If the entities are colliding.
     */
    public boolean collides(Entity other) {
        boolean xOverlap = (this.getMinX() <= other.getMaxX() && this.getMaxX() >= other.getMinX());
        boolean yOverlap = (this.getMinY() <= other.getMaxY() && this.getMaxY() >= other.getMinY());
        boolean zOverlap = (this.getMinZ() <= other.getMaxZ() && this.getMaxZ() >= other.getMinZ());

        return  xOverlap && yOverlap && zOverlap;
    }

    /**
     * Returns the minimal x coordinate of the space this entity occupies determined by its position and size.
     * @return The minimal x coordinate.
     */
    public double getMinX() {
        return this.position.getX() - 0.5 * this.size.getWidth();
    }

    /**
     * Returns the minimal y coordinate of the space this entity occupies determined by its position and size.
     * @return The minimal y coordinate.
     */
    public double getMinY() {
        return this.position.getY() - 0.5 * this.size.getDepth();
    }

    /**
     * Returns the minimal z coordinate of the space this entity occupies determined by its position and size.
     * @return The minimal z coordinate.
     */
    public double getMinZ() {
        return this.position.getZ() - 0.5 * this.size.getHeight();
    }

    /**
     * Returns the maximal x coordinate of the space this entity occupies determined by its position and size.
     * @return The maximal x coordinate.
     */
    public double getMaxX() {
        return this.position.getX() + 0.5 * this.size.getWidth();
    }

    /**
     * Returns the maximal y coordinate of the space this entity occupies determined by its position and size.
     * @return The maximal y coordinate.
     */
    public double getMaxY() {
        return this.position.getY() + 0.5 * this.size.getDepth();
    }

    /**
     * Returns the maximal z coordinate of the space this entity occupies determined by its position and size.
     * @return The maximal z coordinate.
     */
    public double getMaxZ() {
        return this.position.getZ() + 0.5 * this.size.getHeight();
    }

    /**
     * Tests whether this entity and the given entity are equal.
     * @param other The other entity.
     * @return Whether the entities are equal.
     */
     @Override
    public boolean equals(Object other) {
        return other instanceof Entity && this.getId() == ((Entity) other).getId();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

    /**
     * Returns a deep copy of the given entity.
     * @param entity The entity to copy.
     * @return A copy of the entity.
     */
    public static Entity deepcopy(Entity entity) {
        return new Entity(entity.getId(), entity.getSize(), entity.getPosition(), entity.getVelocity(), entity.getAcceleration());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
