package org.inaetics.dronessimulator.gameengine.physicsenginedriver.gameentityupdate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.common.state.HealthGameEntity;
import org.inaetics.dronessimulator.physicsengine.entityupdate.EntityUpdate;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DamageHealthEntityUpdate extends GameEntityUpdate {
    private final int dmg;

    @Override
    public void update(GameEntity entity) {
        if(entity instanceof HealthGameEntity) {
            ((HealthGameEntity) entity).damage(this.dmg);
        }
    }

    @Override
    public EntityUpdate toPhysicsEngineEntityUpdate() {
        // No need to push anything to physicsengine
        return null;
    }
}
