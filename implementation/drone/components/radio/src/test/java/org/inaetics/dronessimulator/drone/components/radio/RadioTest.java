package org.inaetics.dronessimulator.drone.components.radio;

import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.common.protocol.TacticMessage;
import org.inaetics.dronessimulator.common.protocol.TeamTopic;
import org.inaetics.dronessimulator.common.protocol.TextMessage;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.dronessimulator.test.MockPublisher;
import org.inaetics.dronessimulator.test.MockSubscriber;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.inaetics.dronessimulator.test.TestUtils.getConnectedMockPubSub;

public class RadioTest {
    private Radio radio;
    private DroneInit drone;
    private MockPublisher publisher;

    @Before
    public void setUp() throws Exception {
        publisher = new MockPublisher(null, new ArrayList<>());
        drone = new DroneInit();
        radio = new Radio(publisher, drone, "initial topic");
        publisher.setSubscriber(radio);
    }

    @Test
    public void send() throws Exception {
        TextMessage msg = new TextMessage("This is a test message");
        String another = "another test object";

        //send two different messages
        Assert.assertTrue(radio.send(another));
        Assert.assertTrue(radio.send(msg));

        //and assert that we can get them by type
        Assert.assertEquals(msg, radio.getMessage(TextMessage.class));
        Assert.assertEquals(another, radio.getMessage(String.class));

        Assert.assertTrue(publisher.getReceivedMessages().get(0) instanceof RadioMessage);
    }
}