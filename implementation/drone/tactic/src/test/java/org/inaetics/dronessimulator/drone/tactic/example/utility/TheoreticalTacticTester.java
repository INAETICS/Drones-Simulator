package org.inaetics.dronessimulator.drone.tactic.example.utility;

import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.common.model.Triple;
import org.inaetics.dronessimulator.common.protocol.TeamTopic;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.drone.components.engine.Engine;
import org.inaetics.dronessimulator.drone.components.gun.Gun;
import org.inaetics.dronessimulator.drone.components.radio.Radio;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.drone.tactic.TacticTesterHelper;
import org.inaetics.dronessimulator.drone.tactic.example.utility.messages.HeartbeatMessage;
import org.inaetics.dronessimulator.drone.tactic.example.utility.messages.InstructionMessage;
import org.inaetics.dronessimulator.test.MockPublisher;
import org.inaetics.dronessimulator.test.MockSubscriber;
import org.inaetics.dronessimulator.test.TestUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.inaetics.dronessimulator.test.TestUtils.getConnectedMockPubSub;
import static org.inaetics.dronessimulator.test.TestUtils.setField;
import static org.mockito.Mockito.mock;

public class TheoreticalTacticTester {
    private DroneInit droneInit;
    private TheoreticalTactic tactic;
    private MockPublisher publisher;

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TheoreticalTacticTester.class);

    @Before
    public void setup() throws IllegalAccessException, NoSuchFieldException, InstantiationException {
        droneInit = new DroneInit();
        publisher = new MockPublisher();
        tactic = TacticTesterHelper.getTactic(TheoreticalTactic.class, publisher, mock(Discoverer.class), droneInit);
        tactic.initializeTactics();
    }

    @After
    public void teardown() {
        tactic.finalizeTactics();
    }

    @Test
    public void testCalculateTactics() {
        for (int i = 0; i < 10; i++) {
            tactic.calculateTactics();
            tactic.getRadio().receive(new HeartbeatMessage(tactic, tactic.getGps()).getMessage(), null);
        }
        Assert.assertTrue(publisher.getReceivedMessages().size() >= 10);
        Assert.assertThat(publisher.getReceivedMessages(), hasItem(new Tuple<>(new TeamTopic("unknown_team"), new HeartbeatMessage(tactic, tactic.getGps())
                .getMessage())));
        publisher.getReceivedMessages().forEach(message -> log.debug("Message with content: " + message.toString()));
    }

    @Test
    public void testGettingALeader() throws IllegalAccessException, NoSuchFieldException, InstantiationException, InterruptedException {
        MockPublisher publisher = new MockPublisher();
        DroneInit drone1 = new DroneInit();
        DroneInit drone2 = new DroneInit();
        TheoreticalTactic tactic = TacticTesterHelper.getTactic(TheoreticalTactic.class, publisher, mock(Discoverer.class), drone1);
        publisher.addSubscriber(tactic);
        tactic.initializeTactics();
        tactic.startTactic();
        TheoreticalTactic tactic2 = TacticTesterHelper.getTactic(TheoreticalTactic.class, publisher, mock(Discoverer.class), drone2);
        publisher.addSubscriber(tactic2);
        tactic2.initializeTactics();
        tactic2.startTactic();
        for (int i = 0; i < 20; i++) {
            tactic.calculateTactics();
            tactic2.calculateTactics();
        }
        Object leader1 = TestUtils.getField(tactic, "idLeader");
        Object leader2 = TestUtils.getField(tactic2, "idLeader");
        //After a while they should have the same leader
        Assert.assertNotNull(leader1);
        Assert.assertNotNull(leader2);
        Assert.assertEquals(leader1, leader2);

        //When the leader dies, the remaining drone should be its own leader.
        tactic.stopTactic();
        Thread.sleep((long) (TheoreticalTactic.TTL_DRONE * 1000));
        for (int i = 0; i < 20; i++) {
            tactic2.calculateTactics();
        }
        Object leader3 = TestUtils.getField(tactic2, "idLeader");
        Assert.assertNotNull(leader3);
        Assert.assertEquals("The only drone is not its own leader anymore", tactic2.getIdentifier(), leader3);
        tactic2.stopTactic();
    }

    @Test
    public void testCalculateUtility() throws NoSuchFieldException, IllegalAccessException {
        //Create the world
        Map<String, Triple<LocalDateTime, D3Vector, List<String>>> teammembers = new ConcurrentHashMap<>();
        teammembers.put(tactic.getIdentifier(), new Triple<>(LocalDateTime.now(), D3Vector.UNIT, new LinkedList<>(Arrays.asList("radio", "engine"))));
        Queue<Tuple<LocalDateTime, D3Vector>> radarImage = new ConcurrentLinkedQueue<>();
        radarImage.add(new Tuple<>(LocalDateTime.now(), new D3Vector(100, 100, 50)));
        setField(tactic, "teammembers", teammembers);
        setField(tactic, "radarImage", radarImage);
        setField(tactic, "engine", new Engine());
        setField(tactic, "radio", new Radio());

        //Move towards a drone since we have a gun
//        CalculateUtilityHelper.CalculateUtilityParams test1Params = new CalculateUtilityHelper.CalculateUtilityParams(teammembers, mapOfTheWorld, INSTRUCTION_MESSAGE.InstructionType.MOVE, "1", new D3Vector(50, 50, 50));
        tactic.getGps().setPosition(D3Vector.UNIT);
        setField(tactic, "gun", new Gun());
        teammembers.get(tactic.getIdentifier()).getC().add("gun");
        int utility = tactic.calculateUtility(InstructionMessage.InstructionType.MOVE, tactic.getIdentifier(), new D3Vector(50, 50, 50));
        Assert.assertEquals((int) new D3Vector(Settings.ARENA_WIDTH, Settings.ARENA_DEPTH, Settings.ARENA_HEIGHT).length() - (int) new D3Vector(50, 50,
                0).length(), utility);

        //Do not move out of bounds
        tactic.getGps().setPosition(D3Vector.UNIT);
        utility = tactic.calculateUtility(InstructionMessage.InstructionType.MOVE, tactic.getIdentifier(), new D3Vector(-1, -1, -1));
        Assert.assertEquals(-1, utility);

        //Move away from a drone if you do not have a gun. The higher the distance, the better
        tactic.getGps().setPosition(D3Vector.UNIT);
        setField(tactic, "gun", null);
        teammembers.get(tactic.getIdentifier()).getC().remove("gun");
        utility = tactic.calculateUtility(InstructionMessage.InstructionType.MOVE, tactic.getIdentifier(), new D3Vector(50, 50, 50));
        Assert.assertEquals((int) new D3Vector(50, 50, 50).distance_between(radarImage.peek().getRight()), utility);

        //
    }

}
