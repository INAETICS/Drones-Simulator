package org.inaetics.dronessimulator.gameengine.physicsenginedriver.gameentityupdate;


import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.physicsengine.entityupdate.EntityUpdate;

/**
 * Usually, an update is applied either to the gamestate-manager or the physics-engine
 */
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public abstract class GameEntityUpdate {
    /**
     * How to update an entity in the gamestate-manager. Applies the update directly to entity.
     * @param entity Which entity to update.
     */
    public abstract void update(GameEntity entity);

    /**
     * How to update an entity in the physics-engine. The returned value is added to the physics-engine
     * queue.
     * @return The update in terms of the physics-engine
     */
    public abstract EntityUpdate toPhysicsEngineEntityUpdate();
}
