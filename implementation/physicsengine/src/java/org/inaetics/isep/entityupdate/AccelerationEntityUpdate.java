package org.inaetics.isep.entityupdate;


import lombok.AllArgsConstructor;
import org.inaetics.isep.D3Vector;
import org.inaetics.isep.Entity;

@AllArgsConstructor
public class AccelerationEntityUpdate extends EntityUpdate {
    private final D3Vector newAcceleration;

    @Override
    public void update(Entity entity) {
        entity.setAcceleration(this.newAcceleration);
    }
}
