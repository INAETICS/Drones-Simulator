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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.inaetics.dronessimulator.test.TestUtils.getConnectedMockPubSub;

public class RadioTest {
    private Radio radio;
    private TacticMessage simpleMessage;
    private StateMessage stateMessage;
    private MockSubscriber subscriber;
    private DroneInit drone;
    private MockPublisher publisher;
    private Topic initialTopic;

    @Before
    public void setUp() throws Exception {
        Tuple<MockPublisher, MockSubscriber> pubsub = getConnectedMockPubSub();
        subscriber = pubsub.getRight();
        publisher = pubsub.getLeft();
        drone = new DroneInit();
        initialTopic = () -> "initial topic";
        radio = new Radio(subscriber, pubsub.getLeft(), drone, initialTopic);
        simpleMessage = new TacticMessage();
        stateMessage = new StateMessage();
        radio.handleMessage(stateMessage);
        radio.handleMessage(simpleMessage);
    }

    @Test
    public void start() throws Exception {
        radio.start();
        Assert.assertThat(subscriber.getTopics(), hasItem(new TeamTopic(drone.getTeamname())));
        Assert.assertThat(subscriber.getHandlers().get(TextMessage.class), hasItem((MessageHandler<Message>) radio));
        Assert.assertThat(subscriber.getHandlers().get(TacticMessage.class), hasItem((MessageHandler<Message>) radio));
    }

    @Test
    public void send() throws Exception {
        TextMessage msg = new TextMessage("This is a test message");
        Assert.assertTrue(radio.send(msg));
        Assert.assertThat(publisher.getReceivedMessages(), hasItem(new Tuple<>(initialTopic, msg)));
    }

    @Test
    public void handleMessage() throws Exception {
        int messagesBefore = radio.getMessages().size();
        TextMessage testMessage = new TextMessage("Dit is een test");
        radio.handleMessage(testMessage);
        int messagesAfter = radio.getMessages().size();
        Assert.assertEquals(messagesBefore, messagesAfter - 1);
        Assert.assertTrue(radio.getMessages().contains(testMessage));
    }

    @Test
    public void getMessage() throws Exception {
        Assert.assertThat(radio.getMessage(TacticMessage.class), is(simpleMessage));
    }

}