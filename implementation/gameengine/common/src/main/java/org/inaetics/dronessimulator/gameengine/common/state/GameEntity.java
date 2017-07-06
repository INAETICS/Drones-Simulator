package org.inaetics.dronessimulator.gameengine.common.state;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.EntityType;

/**
 * An entity in the physics engine with some added game state.
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public abstract class GameEntity<C extends GameEntity<C>> {
    /** Id of the game entity. Should match with an entity id in the engine. */
    private final int entityId;

    /** Position of the entity in the engine. */
    private volatile D3Vector position;

    /** Velocity of the entity in the engine. */
    private volatile D3Vector velocity;

    /** Acceleration of the entity in the engine. */
    private volatile D3Vector acceleration;

    /**
     * Returns the type of the game entity in terms of the shared protocol.
     * @return The protocol entity type.
     */
    public abstract EntityType getType();

    /**
     * Returns the id of this entity.
     * @return The entity id.
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     * Returns the position of this entity in the world.
     * @return The current position.
     */
    public D3Vector getPosition() {
        return position;
    }

    /**
     * Updates the position of this entity.
     * @param position The new position.
     */
    public void setPosition(D3Vector position) {
        this.position = position;
    }

    /**
     * Returns the velocity of this entity.
     * @return The current velocity.
     */
    public D3Vector getVelocity() {
        return velocity;
    }

    /**
     * Updates the velocity of this entity.
     * @param velocity The new velocity.
     */
    public void setVelocity(D3Vector velocity) {
        this.velocity = velocity;
    }

    /**
     * Returns the acceleration of this entity.
     * @return The current acceleration.
     */
    public D3Vector getAcceleration() {
        return acceleration;
    }

    /**
     * Updates the acceleration of this entity.
     * @param acceleration The new acceleration.
     */
    public void setAcceleration(D3Vector acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * Recursively copies this entity and returns a new entity which is (at the time of creation) equal to this entity.
     * @return The copied entity.
     */
    public abstract C deepCopy();
}
