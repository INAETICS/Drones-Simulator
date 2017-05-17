package org.inaetics.dronessimulator.gameengine.test;

import org.inaetics.dronessimulator.common.D3PoolCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.MovementMessage;
import org.inaetics.dronessimulator.gameengine.SubscriberMessageHandler;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.gameentityupdate.AccelerationUpdate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestGameSubscriberMessageHandler {
    private MockPhysicsEngineDriver mockDriver;
    private SubscriberMessageHandler msgHandler;

    @Before
    public void init() {
        this.mockDriver = new MockPhysicsEngineDriver();
        this.msgHandler = new SubscriberMessageHandler(this.mockDriver);
    }

    @Test
    public void testHandleMessage() {
        MovementMessage msg = new MovementMessage();
        msg.setIdentifier("1");
        msg.setDirection(new D3PoolCoordinate());
        msg.setAcceleration(new D3Vector());

        msgHandler.handleMessage(msg);

        Assert.assertEquals(new AccelerationUpdate(new D3Vector()), mockDriver.getIncomingUpdateQueue().poll());

        MovementMessage msgNoAccell = new MovementMessage();
        msg.setIdentifier("1");
        msg.setDirection(new D3PoolCoordinate());

        msgHandler.handleMessage(msgNoAccell);

        Assert.assertEquals(mockDriver.getIncomingUpdateQueue().poll(), null);
    }

}
