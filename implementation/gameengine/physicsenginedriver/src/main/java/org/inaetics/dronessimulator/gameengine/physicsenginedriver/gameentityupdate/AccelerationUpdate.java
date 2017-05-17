package org.inaetics.dronessimulator.gameengine.physicsenginedriver.gameentityupdate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.physicsengine.entityupdate.AccelerationEntityUpdate;
import org.inaetics.dronessimulator.physicsengine.entityupdate.EntityUpdate;

@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper=true)
public class AccelerationUpdate extends GameEntityUpdate {
    private final D3Vector newAcceleration;

    @Override
    public void update(GameEntity entity) {
        entity.setAcceleration(newAcceleration);
    }

    @Override
    public EntityUpdate toEntityUpdate() {
        return new AccelerationEntityUpdate(this.newAcceleration);
    }
}
