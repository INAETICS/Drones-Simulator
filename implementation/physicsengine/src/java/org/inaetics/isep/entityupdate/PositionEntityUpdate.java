package org.inaetics.isep.entityupdate;


import lombok.AllArgsConstructor;
import org.inaetics.isep.D3Vector;
import org.inaetics.isep.Entity;

@AllArgsConstructor
public class PositionEntityUpdate extends EntityUpdate {
    private final D3Vector newPosition;

    @Override
    public void update(Entity entity) {
        entity.setPosition(this.newPosition);
    }
}
