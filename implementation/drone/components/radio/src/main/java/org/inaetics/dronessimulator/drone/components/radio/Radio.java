package org.inaetics.dronessimulator.drone.components.radio;

import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.pubsub.api.pubsub.Publisher;
import org.inaetics.pubsub.api.pubsub.Subscriber;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Radio component which makes drone to drone communication possible. Messages that are sent are wrapped in a
 * RadioMessage object, which functions as a kind of manual 'topic' so only messages from the right team are received.
 */
public class Radio implements Subscriber {
    /**
     * Reference to Publisher bundle
     */
    private volatile Publisher publisher;
    /**
     * Reference to Drone Init bundle
     */
    private volatile DroneInit drone;
    /**
     * Queue with received messages
     */
    private final ConcurrentLinkedQueue<Object> receivedQueue = new ConcurrentLinkedQueue<>();

    /**
     * Store teamname so only the message from this drone's team are sent/received
     */
    private String teamName;

    //OSGi constructor
    public Radio() {
    }

    //Testing constructor
    public Radio(Publisher publisher, DroneInit drone, String teamName) {
        this.publisher = publisher;
        this.drone = drone;
        this.teamName = teamName;
    }

    /**
     * Create the logger
     */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Radio.class);

    /**
     * Start the Radio (called from Apache Felix). This initializes to what messages the subscriber should listen.
     */
    public void start() {
        teamName = drone.getTeamname();
    }

    /**
     * Send a message to all drones that are subscribed to the topic "TeamTopic(teamname)". Note that the radio should
     * also be subscribed to the type of messages that you send (See: {@link Radio#start()} for the message types that
     * the Radio is subscribed to).
     *
     * @param msg the message to send
     * @return false if there was an error, true otherwise.
     */
    public boolean send(Object msg) {
        publisher.send(new RadioMessage(teamName, msg));
        return true;
    }

    /**
     * Retrieves the queue with received messages
     *
     * @return queue with messages
     */
    public final Queue<Object> getMessages() {
        return receivedQueue;
    }

    /**
     * Receive the messages that the radio is subscribed to and add them to the internal queue.
     */
    @Override
    public void receive(Object o, MultipartCallbacks multipartCallbacks) {
        if (o instanceof RadioMessage) {
            RadioMessage rm = (RadioMessage) o;
            if (rm.getTeamName().equals(teamName)) {
                receivedQueue.add(rm.getMessage());
            }
        }
    }

    /**
     * This method gets the first message in the queue that is of the given class, and removes it from the queue.
     *
     * @param messageClass the class of the message that should be returned
     * @return The found message, or null if there was no message found.
     */
    public final <M> M getMessage(Class<M> messageClass) {
        Optional<Object> messageOptional = getMessages().stream().filter(messageClass::isInstance).findFirst();
        if (messageOptional.isPresent()) {
            Object message = messageOptional.get();
            getMessages().remove(message);
            return messageClass.cast(message);
        }
        return null;
    }
}
