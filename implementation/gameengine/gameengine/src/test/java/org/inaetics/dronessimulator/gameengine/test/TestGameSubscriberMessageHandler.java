package org.inaetics.dronessimulator.gameengine.test;

import org.inaetics.dronessimulator.common.protocol.*;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.gameengine.common.state.Bullet;
import org.inaetics.dronessimulator.gameengine.common.state.Drone;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.GameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapperService;
import org.inaetics.dronessimulator.gameengine.messagehandlers.DamageMessageHandler;
import org.inaetics.dronessimulator.gameengine.messagehandlers.FireBulletMessageHandler;
import org.inaetics.dronessimulator.gameengine.messagehandlers.KillMessageHandler;
import org.inaetics.dronessimulator.gameengine.messagehandlers.MovementMessageHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class TestGameSubscriberMessageHandler {

    private MockPhysicsEngineDriver mockDriver;
    private IdentifierMapperService id_mapper;
    private GameStateManager stateManager;
    private Discoverer mockDiscoverer;

    private DamageMessageHandler damageMessageHandler;
    private FireBulletMessageHandler fireBulletMessageHandler;
    private KillMessageHandler killMessageHandler;
    private MovementMessageHandler movementMessageHandler;

    private Drone drone;

    @Before
    public void init() {
        this.id_mapper = new IdentifierMapperService();
        this.stateManager = new GameStateManager();
        this.mockDriver = new MockPhysicsEngineDriver(this.id_mapper);
        this.mockDiscoverer = mock(Discoverer.class);
        this.damageMessageHandler = new DamageMessageHandler(this.mockDriver, this.id_mapper, this.stateManager);
        this.fireBulletMessageHandler = new FireBulletMessageHandler(this.mockDriver, this.id_mapper, this.stateManager);
        this.killMessageHandler = new KillMessageHandler(this.mockDriver, this.id_mapper, this.stateManager, this.mockDiscoverer);
        this.movementMessageHandler = new MovementMessageHandler(this.mockDriver, this.id_mapper, this.stateManager);

        int gameengineId = this.id_mapper.getNewGameEngineId();
        String protocolId = "1";
        this.drone = new Drone(gameengineId, null, new D3Vector(), new D3Vector(), new D3Vector(), new D3PolarCoordinate());

        this.id_mapper.setMapping(gameengineId, protocolId);
        this.stateManager.addEntityState(drone);
    }

    @Test
    public void testMovementMessage() {
        // Do not set acceleration
        MovementMessage msgNoAccell = new MovementMessage();
        msgNoAccell.setIdentifier("1");
        msgNoAccell.setDirection(new D3PolarCoordinate());

        movementMessageHandler.handleMessage(msgNoAccell);

        Assert.assertEquals(this.mockDriver.getNewAcceleration(), null);

        // Change acceleration
        MovementMessage msg = new MovementMessage();
        msg.setIdentifier("1");
        msg.setDirection(new D3PolarCoordinate());
        msg.setAcceleration(new D3Vector(1,2,3));

        Assert.assertEquals(null, this.mockDriver.getNewAcceleration());

        movementMessageHandler.handleMessage(msg);

        Assert.assertEquals(new D3Vector(1,2,3), this.mockDriver.getNewAcceleration());
    }

    @Test
    public void testDamageMessage() {
        // Try to damage
        DamageMessage damageMessage = new DamageMessage();
        damageMessage.setEntityId("1");
        damageMessage.setEntityType(EntityType.DRONE);
        damageMessage.setDamage(100);

        Assert.assertEquals(-1, mockDriver.getDamaged());
        Assert.assertEquals(-1, mockDriver.getDamage());

        damageMessageHandler.handleMessage(damageMessage);

        Assert.assertEquals(1, mockDriver.getDamaged());
        Assert.assertEquals(100, mockDriver.getDamage());
    }

    @Test
    public void testKillMessage() {
        // Try to kill
        KillMessage killMessage = new KillMessage();
        killMessage.setIdentifier("1");
        killMessage.setEntityType(EntityType.DRONE);

        Assert.assertEquals(-1, mockDriver.getRemoved());

        killMessageHandler.handleMessage(killMessage);

        Assert.assertEquals(1, mockDriver.getRemoved());
    }

    @Test
    public void testFireBullet() {
        FireBulletMessage fireBulletMessage = new FireBulletMessage();

        fireBulletMessage.setIdentifier("BULLET1");
        fireBulletMessage.setType(EntityType.BULLET);
        fireBulletMessage.setPosition(new D3Vector(3,2,1));
        fireBulletMessage.setVelocity(new D3Vector(2,3,1));
        fireBulletMessage.setAcceleration(new D3Vector(1,2,3));
        fireBulletMessage.setDirection(new D3PolarCoordinate());
        fireBulletMessage.setDamage(50);
        fireBulletMessage.setFiredById("1");

        Assert.assertEquals(null, mockDriver.getAdded());

        fireBulletMessageHandler.handleMessage(fireBulletMessage);

        Assert.assertEquals(new Bullet(2, 50, drone, new D3Vector(3,2,1), new D3Vector(2,3,1), new D3Vector(1,2,3), new D3PolarCoordinate()), mockDriver.getAdded());
    }
}
