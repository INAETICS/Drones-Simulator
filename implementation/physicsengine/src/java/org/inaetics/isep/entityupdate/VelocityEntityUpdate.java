package org.inaetics.isep.entityupdate;

import lombok.AllArgsConstructor;
import org.inaetics.isep.D3Vector;
import org.inaetics.isep.Entity;

@AllArgsConstructor
public class VelocityEntityUpdate extends EntityUpdate {
    private final D3Vector newVelocity;

    @Override
    public void update(Entity entity) {
        entity.setVelocity(this.newVelocity);
    }
}
