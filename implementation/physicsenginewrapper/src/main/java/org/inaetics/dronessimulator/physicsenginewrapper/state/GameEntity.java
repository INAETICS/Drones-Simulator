package org.inaetics.dronessimulator.physicsenginewrapper.state;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.physicsengine.Entity;

/**
 * An entity in the physicsengine with some added game state
 */
@Getter
@AllArgsConstructor
public abstract class GameEntity {
    /**
     * Id of the game entity. Should match with an entity id in the engine
     */
    private final int entityId;
    /**
     * Position of the entity in the engine
     */
    private D3Vector position;
    /**
     * Velocity of the entity in the engine
     */
    private D3Vector velocity;
    /**
     * Acceleration of the entity in the engine
     */
    private D3Vector acceleration;

    /**
     * Update the game entity with information from the physics engine
     * @param entity Which physicsengine entity to use as source of information
     */
    public void updateFromEngine(Entity entity) {
        if(entityId == entity.getId()) {
            this.position = entity.getPosition();
            this.velocity = entity.getVelocity();
            this.acceleration = entity.getAcceleration();
        } else {
            Logger.getLogger(GameEntity.class).fatal("Tried to update state from entity, but ids did not match. Received: " + entity.getId() + ". Needed: " + this.entityId);
        }
    }

    /**
     * Get the type of the game entity in terms of the shared protocol
     * @return Type of entity according to protocol
     */
    public abstract EntityType getType();
}
