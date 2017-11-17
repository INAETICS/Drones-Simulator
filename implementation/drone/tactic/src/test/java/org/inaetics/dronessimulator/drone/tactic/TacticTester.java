package org.inaetics.dronessimulator.drone.tactic;

import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.common.protocol.TacticMessage;
import org.inaetics.dronessimulator.common.protocol.TeamTopic;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.drone.tactic.messages.HeartbeatMessage;
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

}
