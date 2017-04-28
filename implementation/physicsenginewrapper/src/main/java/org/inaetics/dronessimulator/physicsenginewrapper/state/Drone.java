package org.inaetics.dronessimulator.physicsenginewrapper.state;

import lombok.Getter;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.EntityType;

@Getter
public class Drone extends PhysicsEngineEntity {
    private int hp;

    public Drone(int id, int hp, D3Vector position, D3Vector velocity, D3Vector acceleration) {
        super(id, position, velocity, acceleration);

        this.hp = hp;
    }

    public void damage(int dmg) {
        this.hp -= dmg;
    }

    @Override
    public EntityType getType() {
        return EntityType.BULLET;
    }
}