package org.inaetics.dronessimulator.gameengine.physicsenginedriver.gameentityupdate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.physicsengine.entityupdate.EntityUpdate;
import org.inaetics.dronessimulator.physicsengine.entityupdate.PositionEntityUpdate;

@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper=true)
public class PositionUpdate extends GameEntityUpdate {
    private final D3Vector newPosition;

    @Override
    public void update(GameEntity entity) {
        // Position should be updated by physicsengine
    }

    @Override
    public EntityUpdate toPhysicsEngineEntityUpdate() {
        return new PositionEntityUpdate(this.newPosition);
    }
}
