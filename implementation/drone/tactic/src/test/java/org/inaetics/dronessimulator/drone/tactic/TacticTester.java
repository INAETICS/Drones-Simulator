package org.inaetics.dronessimulator.drone.tactic;

import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.common.protocol.TacticMessage;
import org.inaetics.dronessimulator.common.protocol.TeamTopic;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.drone.tactic.messages.HeartbeatMessage;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.inaetics.dronessimulator.test.concurrent.MockPublisher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.mockito.Mockito.mock;

@Log4j
public class TacticTester {
    private DroneInit droneInit;
    private TheoreticalTactic tactic;
    private MockPublisher publisher;

    @Before
    public void setup() throws IllegalAccessException, NoSuchFieldException, InstantiationException {
        droneInit = new DroneInit();
        publisher = new MockPublisher();
        Subscriber subscriber = mock(Subscriber.class);
        tactic = TacticTesterHelper.getTactic(TheoreticalTactic.class, publisher, subscriber, droneInit);
        tactic.initializeTactics();
    }

    @Test
    public void testCalculateTactics() {
        for (int i = 0; i < 10; i++) {
            tactic.calculateTactics();
            tactic.radio.handleMessage(new HeartbeatMessage(tactic, tactic.gps).getMessage());
        }
        Assert.assertTrue(publisher.getReceivedMessages().size() >= 10);
        Assert.assertThat(publisher.getReceivedMessages(), hasItem(new Tuple<>(new TeamTopic("unknown_team"), new
                TacticMessage())));
        publisher.getReceivedMessages().forEach(message -> log.debug("Message on topic \"" + message.getLeft().getName
                () + "\" with content: " + message.getRight().toString()));
    }

    @Test
    public void testGettingALeader() throws IllegalAccessException, NoSuchFieldException, InstantiationException, InterruptedException {
        Tuple<Publisher, Subscriber> pubSub = TacticTesterHelper.getConnectedMockPubSub();
        DroneInit drone1 = new DroneInit();
        DroneInit drone2 = new DroneInit();
        Tactic tactic = TacticTesterHelper.getTactic(TheoreticalTactic.class, pubSub.getLeft(), pubSub.getRight(),
                drone1);
        tactic.initializeTactics();
        tactic.startTactic();
        Tactic tactic2 = TacticTesterHelper.getTactic(TheoreticalTactic.class, pubSub.getLeft(), pubSub.getRight(),
                drone2);
        tactic2.initializeTactics();
        tactic2.startTactic();
        for (int i = 0; i < 20 ; i++) {
            try {
                tactic.work();
                log.debug("do work 1");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                tactic2.work();
                log.debug("do work 2");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Object leader1 = TacticTesterHelper.getField(tactic, "idLeader");
        Object leader2 = TacticTesterHelper.getField(tactic2, "idLeader");
        //After a while they should have the same leader
        Assert.assertNotNull(leader1);
        Assert.assertNotNull(leader2);
        Assert.assertEquals(leader1, leader2);

        //When the leader dies, the remaining drone should be its own leader.
        tactic.stopTactic();
        Thread.sleep(TheoreticalTactic.ttlLeader* 1000);
        for (int i = 0; i < 20; i++) {
            try {
                tactic2.work();
                log.debug("do work 2");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Object leader3 = TacticTesterHelper.getField(tactic2, "idLeader");
        Assert.assertNotNull(leader3);
        Assert.assertEquals("The only drone is not its own leader anymore",tactic2.getIdentifier(), leader3);
        tactic2.stopTactic();
    }

}
