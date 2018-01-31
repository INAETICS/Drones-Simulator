package org.inaetics.dronessimulator.physicsengine;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.Size;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;

/**
 * An entity represented in the simulated world.
 * x == width, y == depth, z == height
 */

@Getter
@Setter
@ToString
public class Entity extends GameEntity {
    /**
     * Creates an entity.
     *
     * @param id   The id of the new entity.
     * @param size The size of the non-rotating hitbox of the entity.
     */
    public Entity(int id, Size size) {
        super(id, size, new D3Vector(), new D3Vector(), new D3Vector(), new D3PolarCoordinate());
    }

    /**
     * Creates an entity.
     *
     * @param id   The id of the new entity.
     * @param size The size of the non-rotating hitbox of the entity.
     * @param x    The position of the new entity on the x-axis in the world.
     * @param y    The position of the new entity on the y-axis in the world.
     * @param z    The position of the new entity on the z-axis in the world.
     */
    public Entity(int id, Size size, double x, double y, double z) {
        super(id, size, new D3Vector(x, y, z), new D3Vector(), new D3Vector(), new D3PolarCoordinate());
    }

    /**
     * Creates an entity.
     *
     * @param id       The id of the new entity.
     * @param size     The size of the non-rotating hitbox of the entity.
     * @param position The position of the new entity in the world.
     */
    public Entity(int id, Size size, D3Vector position) {
        this(id, size, position, new D3Vector(), new D3Vector(), new D3PolarCoordinate());
    }

    /**
     * Creates an entity.
     *
     * @param id       The id of the new entity.
     * @param size     The size of the non-rotating hitbox of the entity.
     * @param position The position of the new entity in the world.
     * @param velocity The velocity of the new entity in the world.
     */
    public Entity(int id, Size size, D3Vector position, D3Vector velocity) {
        super(id, size, position, velocity, new D3Vector(), new D3PolarCoordinate());
    }

    /**
     * Creates an entity.
     *
     * @param id        The id of the new entity.
     * @param size      The size of the non-rotating hitbox of the entity.
     * @param position  The position of the new entity in the world.
     * @param velocity  The velocity of the new entity in the world.
     * @param direction The direction of the new entity in the world.
     */
    public Entity(int id, Size size, D3Vector position, D3Vector velocity, D3PolarCoordinate direction) {
        super(id, size, position, velocity, new D3Vector(), direction);
    }

    public Entity(int entityId, Size size, D3Vector position, D3Vector velocity, D3Vector acceleration, D3PolarCoordinate direction) {
        super(entityId, size, position, velocity, acceleration, direction);
    }

    public Entity(Entity entity) {
        this(entity.getEntityId(), entity.getSize(), entity.getPosition(), entity.getVelocity(), entity
                .getAcceleration(), entity.getDirection());
    }

    /**
     * Moves the entity in the world using the set velocity and acceleration for the time step.
     *
     * @param time_in_seconds The time step in seconds.
     */
    public void move(double time_in_seconds) {
        setVelocity(this.nextVelocity(getAcceleration(), time_in_seconds));
        setPosition(this.nextPosition(getVelocity(), time_in_seconds));
    }

    /**
     * Calculates the velocity for this entity after the given period of the given acceleration.
     *
     * @param nextAcceleration The change in velocity during time step.
     * @param time_in_seconds  The time step is in seconds.
     * @return The next velocity for this entity.
     */
    public D3Vector nextVelocity(D3Vector nextAcceleration, double time_in_seconds) {
        return nextAcceleration.scale(time_in_seconds).add(getVelocity());
    }

    /**
     * Calculates the position of this entity after the given period of the given velocity.
     *
     * @param nextVelocity    The change in position during this time step.
     * @param time_in_seconds How long the time step is in seconds.
     * @return The next position for this entity.
     */
    public D3Vector nextPosition(D3Vector nextVelocity, double time_in_seconds) {
        return nextVelocity.scale(time_in_seconds).add(getPosition());
    }

    /**
     * Test whether this entity and the given entity are colliding at their current position.
     *
     * @param other The other entity.
     * @return If the entities are colliding.
     */
    public boolean collides(Entity other) {
        boolean xOverlap = (this.getMinX() <= other.getMaxX() && this.getMaxX() >= other.getMinX());
        boolean yOverlap = (this.getMinY() <= other.getMaxY() && this.getMaxY() >= other.getMinY());
        boolean zOverlap = (this.getMinZ() <= other.getMaxZ() && this.getMaxZ() >= other.getMinZ());

        return xOverlap && yOverlap && zOverlap;
    }

    /**
     * Returns the minimal x coordinate of the space this entity occupies determined by its position and size.
     *
     * @return The minimal x coordinate.
     */
    public double getMinX() {
        return getPosition().getX() - 0.5 * getSize().getWidth();
    }

    /**
     * Returns the minimal y coordinate of the space this entity occupies determined by its position and size.
     *
     * @return The minimal y coordinate.
     */
    public double getMinY() {
        return getPosition().getY() - 0.5 * getSize().getDepth();
    }

    /**
     * Returns the minimal z coordinate of the space this entity occupies determined by its position and size.
     *
     * @return The minimal z coordinate.
     */
    public double getMinZ() {
        return getPosition().getZ() - 0.5 * getSize().getHeight();
    }

    /**
     * Returns the maximal x coordinate of the space this entity occupies determined by its position and size.
     *
     * @return The maximal x coordinate.
     */
    public double getMaxX() {
        return getPosition().getX() + 0.5 * getSize().getWidth();
    }

    /**
     * Returns the maximal y coordinate of the space this entity occupies determined by its position and size.
     *
     * @return The maximal y coordinate.
     */
    public double getMaxY() {
        return getPosition().getY() + 0.5 * getSize().getDepth();
    }

    /**
     * Returns the maximal z coordinate of the space this entity occupies determined by its position and size.
     *
     * @return The maximal z coordinate.
     */
    public double getMaxZ() {
        return getPosition().getZ() + 0.5 * getSize().getHeight();
    }

    /**
     * Tests whether this entity and the given entity are equal.
     *
     * @param other The other entity.
     * @return Whether the entities are equal.
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof Entity && this.getEntityId() == ((Entity) other).getEntityId();
    }

    @Override
    public int hashCode() {
        return this.getEntityId();
    }

    @Override
    public EntityType getType() {
        return null;
    }

    /**
     * Returns a deep copy of the curent entity.
     *
     * @return A copy of the entity.
     */
    @Override
    public GameEntity deepCopy() {
        return new Entity(this);
    }


    @Log4j
    public static class DroneEntity extends Entity {

        @Getter
        @Setter
        private D3Vector targetPosition;

        public DroneEntity(int id, Size size, D3Vector position, D3Vector velocity, D3Vector acceleration, D3PolarCoordinate direction, D3Vector targetPosition) {
            super(id, size, position, velocity, acceleration, direction);
            this.targetPosition = targetPosition;
        }


    }
}
