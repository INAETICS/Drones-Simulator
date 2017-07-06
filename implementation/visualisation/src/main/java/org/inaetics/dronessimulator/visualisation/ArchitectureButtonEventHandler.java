package org.inaetics.dronessimulator.visualisation;


import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.RequestArchitectureStateChangeMessage;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;

import java.io.IOException;


public class ArchitectureButtonEventHandler implements EventHandler<MouseEvent> {
    /** The Logger */
    private final static Logger logger = Logger.getLogger(ArchitectureButtonEventHandler.class);
    /** The publisher */
    private final Publisher publisher;
    /** Action that will be taken when clicking the button */
    private final SimulationAction action;

    /**
     * Instantiates a button that will send a architecture change message based on its action
     * @param action Action the button has to send
     * @param publisher Publisher that will publish the action to rabbitmq
     */
    ArchitectureButtonEventHandler(SimulationAction action, Publisher publisher) {
        this.action = action;
        this.publisher = publisher;
    }

    /**
     * Sends an architecture change message when the button is clicked
     * @param event
     */
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
