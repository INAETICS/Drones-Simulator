package org.inaetics.dronessimulator.drone.components.radio;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.protocol.TextMessage;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;


import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Radio implements MessageHandler {
    private static final Logger logger = Logger.getLogger(Radio.class);

    private volatile Subscriber m_subscriber;
    private volatile Publisher m_publisher;
    private volatile DroneInit m_drone;
    private ConcurrentLinkedQueue<String> received_queue = new ConcurrentLinkedQueue<String>();

    /**
     * FELIX CALLBACKS
     */
    public void start() {
        try {
            this.m_subscriber.addTopic(MessageTopic.RADIO);
        } catch (IOException e) {
            logger.fatal(e);
        }
        this.m_subscriber.addHandler(StateMessage.class, this);
    }

    public void sendText(String text){
        TextMessage msg = new TextMessage();
        msg.setText(text);
        try{
            m_publisher.send(MessageTopic.RADIO, msg);
        } catch(IOException e){
            logger.fatal(e);
        }
    }

    public ConcurrentLinkedQueue<String> getMessages(){
        return received_queue;
    }

    /**
     * -- MESSAGEHANDLER
     */
    public void handleMessage(Message message) {
        if (message instanceof TextMessage){
            TextMessage textMessage = (TextMessage) message;
            received_queue.add(textMessage.getText());

        }
    }

}