package org.inaetics.dronessimulator.visualisation;


import javafx.scene.input.MouseEvent;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.RequestArchitectureStateChangeMessage;
import org.inaetics.dronessimulator.test.concurrent.MockPublisher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class ArchitectureButtonEventHandlerTest {
    private ArchitectureButtonEventHandler handler;
    private MockPublisher publisher;


    @Before
    public void setup() {
        publisher = new MockPublisher();
        handler = new ArchitectureButtonEventHandler(SimulationAction.INIT, publisher);
    }

    @Test
    public void testHandle() {
        //If a mouse event occurs, this should be added to the topic Architecture with the action specified in the handler.
        handler.handle(mock(MouseEvent.class));

        Assert.assertTrue(publisher.isMessageReceived(MessageTopic.ARCHITECTURE, new RequestArchitectureStateChangeMessage(SimulationAction.INIT)));
    }
}
