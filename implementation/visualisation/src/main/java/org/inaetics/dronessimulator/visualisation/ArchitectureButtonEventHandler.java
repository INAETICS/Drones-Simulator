package org.inaetics.dronessimulator.visualisation;


import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.protocol.RequestArchitectureStateChangeMessage;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;

import java.io.IOException;


public class ArchitectureButtonEventHandler implements EventHandler<MouseEvent> {
    private final static Logger logger = Logger.getLogger(ArchitectureButtonEventHandler.class);
    private final Publisher publisher;
    private final SimulationAction action;

    public ArchitectureButtonEventHandler(SimulationAction action, Publisher publisher) {
        this.action = action;
        this.publisher = publisher;
    }

    @Override
    public void handle(MouseEvent event) {
        RequestArchitectureStateChangeMessage msg = new RequestArchitectureStateChangeMessage();
        msg.setAction(action);

        try {
            publisher.send(MessageTopic.ARCHITECTURE, msg);
        } catch (IOException e) {
            logger.fatal(e);
        }
    }
}
