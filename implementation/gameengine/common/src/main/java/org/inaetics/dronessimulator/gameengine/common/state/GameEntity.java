package org.inaetics.dronessimulator.gameengine.common.state;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.pubsub.protocol.EntityType;

/**
 * An entity in the physics engine with some added game state.
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public abstract class GameEntity<C extends GameEntity<C>> {
    /** Id of the game entity. Should match with an entity id in the engine. */
    @Getter
    private final int entityId;

    /** Position of the entity in the engine. */
    @Getter @Setter
    private volatile D3Vector position;

    /** Velocity of the entity in the engine. */
    @Getter @Setter
    private volatile D3Vector velocity;

    /** Acceleration of the entity in the engine. */
    @Getter @Setter
    private volatile D3Vector acceleration;

    /** Direction of the entity in the engine. */
    @Getter @Setter
    private volatile D3PolarCoordinate direction;

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
}
