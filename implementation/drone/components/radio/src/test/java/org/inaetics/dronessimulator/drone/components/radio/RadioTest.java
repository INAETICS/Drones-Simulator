package org.inaetics.dronessimulator.drone.components.radio;

import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.common.protocol.TacticMessage;
import org.inaetics.dronessimulator.common.protocol.TextMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class RadioTest {
    private Radio radio;
    private TacticMessage simpleMessage;
    private StateMessage stateMessage;

    @Before
    public void setUp() throws Exception {
        radio = new Radio();
        simpleMessage = new TacticMessage();
        stateMessage = new StateMessage();
        radio.handleMessage(stateMessage);
        radio.handleMessage(simpleMessage);
    }

    @Test
    public void start() throws Exception {
    }

    @Test
    public void sendText() throws Exception {
    }

    @Test
    public void send() throws Exception {
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