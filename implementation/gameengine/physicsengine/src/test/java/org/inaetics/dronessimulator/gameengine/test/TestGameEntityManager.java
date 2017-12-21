package org.inaetics.dronessimulator.gameengine.test;

import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.Size;
import org.inaetics.dronessimulator.physicsengine.Entity;
import org.inaetics.dronessimulator.physicsengine.EntityManager;
import org.inaetics.dronessimulator.physicsengine.entityupdate.AccelerationEntityUpdate;
import org.inaetics.dronessimulator.physicsengine.entityupdate.PositionEntityUpdate;
import org.inaetics.dronessimulator.physicsengine.entityupdate.VelocityEntityUpdate;
import org.inaetics.dronessimulator.test.concurrent.ConcurrentExecute;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Set;

public class TestGameEntityManager {
    private EntityManager manager;

    @Before
    public void init() {
        HashMap<Integer, Set<Integer>> currentCollisions = new HashMap<>();
        this.manager = new EntityManager(currentCollisions);
    }

    @Test
    public void testConcurrentModifications() {
        ConcurrentExecute concurrentExecuteAdd = new ConcurrentExecute(100);
        ConcurrentExecute concurrentExecuteUpdate = new ConcurrentExecute(100);
        ConcurrentExecute concurrentExecuteProcess = new ConcurrentExecute(1);

        for(int i = 0; i < 300; i++) {
            final int i_ = i;
            concurrentExecuteAdd.addJob(1, 100, (j) -> this.manager.addInsert(new Entity(i_, new Size(0, 0, 0))));
        }

        for(int i = 0; i < 300; i++) {
            final int i_ = i;
            concurrentExecuteUpdate.addJob(1, 100, (j) -> {
                this.manager.addUpdate(i_, new AccelerationEntityUpdate(new D3Vector(1, 3, 2)));
                this.manager.addUpdate(i_, new VelocityEntityUpdate(new D3Vector(3, 2, 1)));
                this.manager.addUpdate(i_, new PositionEntityUpdate(new D3Vector(1, 2, 3)));
            });
        }

        concurrentExecuteProcess.addJob(600, 100, (j) -> {
            this.manager.processChanges();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        concurrentExecuteAdd.start();
        concurrentExecuteProcess.start();
        concurrentExecuteAdd.waitTillDone();

        concurrentExecuteUpdate.start();
        concurrentExecuteUpdate.waitTillDone();

        concurrentExecuteProcess.waitTillDone();

        // Just in case finish any waiting changes
        this.manager.processChanges();

        for(Entity entity : this.manager.copyState()) {
            Assert.assertEquals(new D3Vector(1,3,2), entity.getAcceleration());
            Assert.assertEquals(new D3Vector(3, 2,1), entity.getVelocity());
            Assert.assertEquals(new D3Vector(1,2,3), entity.getPosition());
        }
    }
}
