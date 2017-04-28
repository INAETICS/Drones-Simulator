package org.inaetics.dronessimulator.physicsenginewrapper.state;

import lombok.Getter;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.EntityType;

@Getter
public class Bullet extends PhysicsEngineEntity {
    private final int dmg;

    public Bullet(int id, int dmg, D3Vector position, D3Vector velocity, D3Vector acceleration) {
        super(id, position, velocity, acceleration);

        this.dmg = dmg;
    }

    @Override
    public EntityType getType() {
        return EntityType.DRONE;
    }
}
