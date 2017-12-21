package org.inaetics.dronessimulator.drone.components.radio;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.protocol.TacticMessage;
import org.inaetics.dronessimulator.common.protocol.TeamTopic;
import org.inaetics.dronessimulator.common.protocol.TextMessage;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.api.Topic;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Radio component wich makes drone to drone communication possible.
 */
@Log4j
@NoArgsConstructor //OSGi constructor
@AllArgsConstructor //Testing constructor
public class Radio implements MessageHandler {
    /** Reference to Subscriber bundle */
    private volatile Subscriber m_subscriber;
    /** Reference to Publisher bundle */
    private volatile Publisher m_publisher;
    /** Reference to Drone Init bundle */
    private volatile DroneInit m_drone;
    /** Queue with received messages */
    private ConcurrentLinkedQueue<Message> received_queue = new ConcurrentLinkedQueue<>();

    private Topic topic;

    /**
     * Start the Radio (called from Apache Felix). This initializes to what messages the subscriber should listen.
     */
    public void start() {
        topic = new TeamTopic(m_drone.getTeamname());
        try {
            this.m_subscriber.addTopic(topic);
        } catch (IOException e) {
            log.fatal(e);
        }
        this.m_subscriber.addHandler(TextMessage.class, this);
        this.m_subscriber.addHandler(TacticMessage.class, this);
    }

    /**
     * Send a message to all drones that are subscribed to the topic "TeamTopic(teamname)". Note that the radio should also be subscribed to the type of messages that
     * you send (See: {@link Radio#start()} for the message types that the Radio is subscribed to).
     *
     * @param msg the message to send
     * @return false if there was an error, true otherwise.
     */
    public boolean send(Message msg) {
        try {
            m_publisher.send(topic, msg);
            return true;
        } catch (IOException e) {
            log.fatal(e);
            return false;
        }
    }

    /**
     * Retrieves the queue with received messages
     *
     * @return queue with messages
     */
    public final ConcurrentLinkedQueue<Message> getMessages() {
        return received_queue;
    }

    /**
     * Receive the messages that the radio is subscribed to and add them to the internal queue.
     */
    public void handleMessage(Message message) {
        received_queue.add(message);
    }

    /**
     * This method gets the first message in the queue that is of the given class, and removes it from the queue.
     *
     * @param messageClass the class of the message that should be returned
     * @return The found message, or null if there was no message found.
     */
    public final <M extends Message> M getMessage(Class<M> messageClass) {
        Optional<Message> messageOptional = getMessages().stream().filter(messageClass::isInstance).findFirst();
        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();
            getMessages().remove(message);
            return messageClass.cast(message);
        }
        return null;
    }
}
