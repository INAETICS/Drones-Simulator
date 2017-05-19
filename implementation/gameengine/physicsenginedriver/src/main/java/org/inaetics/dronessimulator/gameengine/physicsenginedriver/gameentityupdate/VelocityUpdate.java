package org.inaetics.dronessimulator.gameengine.physicsenginedriver.gameentityupdate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.physicsengine.entityupdate.EntityUpdate;
import org.inaetics.dronessimulator.physicsengine.entityupdate.VelocityEntityUpdate;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class VelocityUpdate extends GameEntityUpdate {
    private final D3Vector newVelocity;

    @Override
    public void update(GameEntity entity) {
        entity.setVelocity(newVelocity);
    }

    @Override
    public EntityUpdate toEntityUpdate() {
        return new VelocityEntityUpdate(this.newVelocity);
    }
}
