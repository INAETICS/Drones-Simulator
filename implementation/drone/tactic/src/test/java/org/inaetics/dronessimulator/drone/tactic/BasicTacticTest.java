package org.inaetics.dronessimulator.drone.tactic;

import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.tactic.example.basic.BasicTactic;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class BasicTacticTest {

    @Test
    @Ignore
    public void testCalculateMovement() {

        // accelerate towards target from stationary
        D3Vector pos1 = new D3Vector(0, 0, 0);
        D3Vector tar1 = new D3Vector(200, 0, 0);
        D3Vector vel1 = new D3Vector(0, 0, 0);

        D3Vector res1 = BasicTactic.calculateMovement(pos1, tar1, vel1);
        Assert.assertEquals(new D3Vector(200, 0, 0), res1);

        // accelerate towards target from moving
        D3Vector pos2 = new D3Vector(0, 0, 0);
        D3Vector tar2 = new D3Vector(200, 0, 0);
        D3Vector vel2 = new D3Vector(20, 0, 0);

        D3Vector res2 = BasicTactic.calculateMovement(pos2, tar2, vel2);
        Assert.assertEquals(new D3Vector(200, 0, 0), res2);

        // decelerate from moving to moving
        D3Vector pos3 = new D3Vector(180, 0, 0);
        D3Vector tar3 = new D3Vector(200, 0, 0);
        D3Vector vel3 = new D3Vector(20, 0, 0);

        D3Vector res3 = BasicTactic.calculateMovement(pos3, tar3, vel3);
        Assert.assertEquals(new D3Vector(-10, 0, 0), res3);

        // decelerate from moving to stationary
        D3Vector pos4 = new D3Vector(199, 0, 0);
        D3Vector tar4 = new D3Vector(200, 0, 0);
        D3Vector vel4 = new D3Vector(1, 0, 0);

        D3Vector res4 = BasicTactic.calculateMovement(pos4, tar4, vel4);
        Assert.assertEquals(new D3Vector(1, 0, 0), res4);

        // stand still on target
        D3Vector pos5 = new D3Vector(50, 0, 0);
        D3Vector tar5 = new D3Vector(50.9, 0, 0);
        D3Vector vel5 = new D3Vector(0, 0, 0);

        D3Vector res5 = BasicTactic.calculateMovement(pos5, tar5, vel5);
        Assert.assertEquals(new D3Vector(0, 0, 0), res5);

        // move just outside of target
        D3Vector pos6 = new D3Vector(51, 0, 0);
        D3Vector tar6 = new D3Vector(50, 0, 0);
        D3Vector vel6 = new D3Vector(1, 0, 0);

        D3Vector res6 = BasicTactic.calculateMovement(pos6, tar6, vel6);
        Assert.assertEquals(new D3Vector(0, 0, 0), res6);
    }

}