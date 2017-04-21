package org.inaetics.dronessimulator.physicsengine.entityupdate;

import lombok.AllArgsConstructor;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.physicsengine.Entity;


@AllArgsConstructor
public class VelocityEntityUpdate extends EntityUpdate {
    private final D3Vector newVelocity;

    @Override
    public void update(Entity entity) {
        entity.setVelocity(this.newVelocity);
    }
}
