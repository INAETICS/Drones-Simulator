package org.inaetics.dronessimulator.gameengine.common.state;

import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.Size;

import java.util.Objects;

/**
 * An entity in the physics engine with some added game state.
 */
//@EqualsAndHashCode(callSuper=false)
public abstract class GameEntity<C extends GameEntity<C>> {
    public GameEntity(int entityId, Size size) {
        this.entityId = entityId;
        this.size = size;
    }

    public GameEntity(int entityId, Size size, D3Vector position, D3Vector velocity, D3Vector acceleration, D3PolarCoordinate direction) {
        this.entityId = entityId;
        this.size = size;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.direction = direction;
    }

    /** Id of the game entity. Should match with an entity id in the engine. */
    private final int entityId;

    public int getEntityId() {
        return entityId;
    }

    /** The size of the non-rotating hitbox around the entity. */
    private final Size size;

    public Size getSize() {
        return size;
    }

    /** Position of the entity in the engine. */
    private volatile D3Vector position;

    public D3Vector getPosition() {
        return position;
    }

    public void setPosition(D3Vector position) {
        this.position = position;
    }

    /** Velocity of the entity in the engine. */
    private volatile D3Vector velocity;

    public D3Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(D3Vector velocity) {
        this.velocity = velocity;
    }

    /** Acceleration of the entity in the engine. */
    private volatile D3Vector acceleration;

    public D3Vector getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(D3Vector acceleration) {
        this.acceleration = acceleration;
    }

    /** Direction of the entity in the engine. */
    private volatile D3PolarCoordinate direction;

    public D3PolarCoordinate getDirection() {
        return direction;
    }

    public void setDirection(D3PolarCoordinate direction) {
        this.direction = direction;
    }

    /**
     * Returns the type of the game entity in terms of the shared protocol.
     * @return The protocol entity type.
     */
    public abstract EntityType getType();

    /**
     * Recursively copies this entity and returns a new entity which is (at the time of creation) equal to this entity.
     * @return The copied entity.
     */
    public abstract C deepCopy();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameEntity)) return false;
        GameEntity<?> that = (GameEntity<?>) o;
        return entityId == that.entityId &&
                Objects.equals(size, that.size) &&
                Objects.equals(position, that.position) &&
                Objects.equals(velocity, that.velocity) &&
                Objects.equals(acceleration, that.acceleration) &&
                Objects.equals(direction, that.direction);
    }

    @Override
    public int hashCode() {

        return Objects.hash(entityId, size, position, velocity, acceleration, direction);
    }
}
