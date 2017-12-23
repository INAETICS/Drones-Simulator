package org.inaetics.dronessimulator.drone.components.radar;

import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.common.protocol.*;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.discovery.api.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.MockDiscoverer;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.test.MockSubscriber;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.CoreMatchers.*;
import static org.inaetics.dronessimulator.test.TestUtils.getField;
import static org.inaetics.dronessimulator.test.TestUtils.setField;
import static org.mockito.Mockito.mock;

public class RadarTest {
    private Radar radar;
    private DroneInit drone;
    private MockDiscoverer discoverer;
    private MockSubscriber subscriber;

    @Before
    public void setUp() throws Exception {
        drone = new DroneInit();
        drone.setIdentifier("1");
        discoverer = new MockDiscoverer();
        subscriber = new MockSubscriber();
        radar = new Radar(mock(ArchitectureEventController.class), subscriber, drone, discoverer, D3Vector.UNIT);
    }

    @Test
    public void testStart() throws Exception {
        radar.start();
        Map<String, D3Vector> allEntities = new ConcurrentHashMap<>();
        setField(radar, "allEntities", allEntities);
        allEntities.put("drone-id", D3Vector.ZERO);

        Assert.assertThat(discoverer.getRemovedHandlers().size(), is(1));
        Assert.assertThat(subscriber.getHandlers().get(StateMessage.class), hasItem((MessageHandler<Message>) radar));
        Assert.assertThat(subscriber.getHandlers().get(KillMessage.class), hasItem((MessageHandler<Message>) radar));
        Assert.assertThat(subscriber.getTopics(), hasItem(MessageTopic.STATEUPDATES));

        DiscoveryNode node = new DiscoveryNode("drone-id", new DiscoveryNode("root"), DiscoveryPath.config(Type.DRONE, Group.DRONE, "name"));
        RemovedNode removedNode = new RemovedNode(node);
        discoverer.getRemovedHandlers().get(0).handle(removedNode);
        Assert.assertThat(allEntities.values(), not(hasItem(D3Vector.ZERO)));
        Assert.assertThat(allEntities.size(), is(0));

        Object[] radarStateBefore = getRadarState(radar);
        removedNode = new RemovedNode(new DiscoveryNode("random node", new DiscoveryNode("root"), DiscoveryPath.config(Type.SERVICE, Group.TACTIC, "some tactic")));
        discoverer.getRemovedHandlers().get(0).handle(removedNode);
        Assert.assertThat(radarStateBefore, is(getRadarState(radar)));
    }

    @Test
    public void testGetRadar() throws Exception {
        Map<String, D3Vector> allEntities = new ConcurrentHashMap<>();
        setField(radar, "allEntities", allEntities);

        allEntities.put("in-range", new D3Vector(100, 100, 100));
        Assert.assertThat(radar.getRadar(), hasItems(allEntities.values().toArray(new D3Vector[allEntities.size()])));

        allEntities.put("Out-of-range", new D3Vector(800, 800, 800));
        Assert.assertThat(radar.getRadar(), not(hasItem(new D3Vector(800, 800, 800))));

        setField(radar, "position", null);
        Assert.assertEquals(Collections.emptyList(), radar.getRadar());
    }

    @Test
    public void testGetNearestTarget() throws Exception {
        Map<String, D3Vector> allEntities = new ConcurrentHashMap<>();
        setField(radar, "allEntities", allEntities);

        allEntities.put("far", new D3Vector(100, 100, 100));
        allEntities.put("near", new D3Vector(10, 10, 10));
        allEntities.put("Out-of-range", new D3Vector(800, 800, 800));
        Optional<D3Vector> target = radar.getNearestTarget();
        if (target.isPresent()) {
            Assert.assertThat(target.get(), is(new D3Vector(10, 10, 10)));
        } else {
            Assert.fail("No nearest target found");
        }
    }

    @Test
    public void testHandleStateMessage() throws Exception {
        StateMessage stateMessageDronePosition = new StateMessage();
        stateMessageDronePosition.setIdentifier(drone.getIdentifier());
        stateMessageDronePosition.setPosition(new D3Vector(50, 50, 50));
        radar.handleMessage(stateMessageDronePosition);
        Assert.assertThat(radar.getPosition(), is(new D3Vector(50, 50, 50)));

        StateMessage stateMessageOtherDrones = new StateMessage();
        stateMessageOtherDrones.setIdentifier("other-drone");
        stateMessageOtherDrones.setType(EntityType.DRONE);
        stateMessageOtherDrones.setPosition(new D3Vector(125, 125, 125));
        radar.handleMessage(stateMessageOtherDrones);
        Assert.assertThat(radar.getRadar(), hasItem(new D3Vector(125, 125, 125)));

        Object[] radarStateBeforeUselessMessage = getRadarState(radar);
        StateMessage uselessStateMessage = new StateMessage();
        uselessStateMessage.setIdentifier("bullet");
        uselessStateMessage.setType(EntityType.BULLET);
        radar.handleMessage(uselessStateMessage);
        Assert.assertThat(radarStateBeforeUselessMessage, is(getRadarState(radar)));

        radarStateBeforeUselessMessage = getRadarState(radar);
        radar.handleMessage(new MovementMessage());
        Assert.assertThat(radarStateBeforeUselessMessage, is(getRadarState(radar)));
    }

    @Test
    public void testHandleKillMessage() throws Exception {
        Map<String, D3Vector> allEntities = new ConcurrentHashMap<>();
        setField(radar, "allEntities", allEntities);
        allEntities.put("far", new D3Vector(100, 100, 100));
        KillMessage killMessage = new KillMessage();
        killMessage.setIdentifier("far");
        killMessage.setEntityType(EntityType.DRONE);
        radar.handleMessage(killMessage);
        Assert.assertThat(radar.getRadar(), not(hasItem(new D3Vector(100, 100, 100))));

        //Killing the drone self, nothing happens since the drone itself is not part of the radar image
        Object[] radarStateBeforeUselessMessage = getRadarState(radar);
        KillMessage killMessageSelf = new KillMessage();
        killMessageSelf.setIdentifier(drone.getIdentifier());
        killMessageSelf.setEntityType(EntityType.DRONE);
        radar.handleMessage(killMessageSelf);
        Assert.assertThat(radarStateBeforeUselessMessage, is(getRadarState(radar)));

        radarStateBeforeUselessMessage = getRadarState(radar);
        KillMessage uselessKillMessage = new KillMessage();
        uselessKillMessage.setIdentifier("non-existing-drone");
        uselessKillMessage.setEntityType(EntityType.DRONE);
        radar.handleMessage(uselessKillMessage);
        Assert.assertThat(radarStateBeforeUselessMessage, is(getRadarState(radar)));
    }

    private Object[] getRadarState(final Radar radar) throws NoSuchFieldException, IllegalAccessException {
        Object[] fields = new Object[6];
        fields[0] = getField(radar, "allEntities");
        fields[1] = getField(radar, "architectureEventController");
        fields[2] = getField(radar, "subscriber");
        fields[3] = getField(radar, "drone");
        fields[4] = getField(radar, "discoverer");
        fields[5] = getField(radar, "position");
        return fields;
    }
}