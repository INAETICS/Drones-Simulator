package org.inaetics.dronessimulator.gameengine.physicsenginedriver.gameentityupdate;


import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.physicsengine.entityupdate.EntityUpdate;

public abstract class GameEntityUpdate {
    /**
     * How to update an entity. Applies the update directly to entity.
     * @param entity Which entity to update.
     */
    public abstract void update(GameEntity entity);

    public abstract EntityUpdate toEntityUpdate();
}
