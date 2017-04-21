package org.inaetics.dronessimulator.physicsengine.entityupdate;


import lombok.AllArgsConstructor;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.physicsengine.Entity;

@AllArgsConstructor
public class AccelerationEntityUpdate extends EntityUpdate {
    private final D3Vector newAcceleration;

    @Override
    public void update(Entity entity) {
        entity.setAcceleration(this.newAcceleration);
    }
}
