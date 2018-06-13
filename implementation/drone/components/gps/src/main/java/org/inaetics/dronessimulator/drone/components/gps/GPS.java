package org.inaetics.dronessimulator.drone.components.gps;

import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.pubsub.api.pubsub.Subscriber;

import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * The GPS drone component
 */
public class GPS implements Subscriber {
    private final Set<GPSCallback> callbacks = new HashSet<>();
    /** The Subscriber to use for receiving messages */
    private volatile Subscriber subscriber;
    /** The drone instance that can be used to get information about the current drone */
    private volatile DroneInit drone;
    private StateMessage previousMessage;
    //OSGi constructor
    public GPS() {
    }
    //Testing constructor
    public GPS(Subscriber subscriber, DroneInit drone, StateMessage previousMessage, D3Vector position, D3Vector velocity, D3Vector acceleration, D3PolarCoordinate direction) {
        this.subscriber = subscriber;
        this.drone = drone;
        this.previousMessage = previousMessage;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.direction = direction;
    }

    /**
     * Create the logger
     */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(GPS.class);
    /**
     * Last known position of this drone in the architecture
     */
    private volatile D3Vector position = new D3Vector();

    public D3Vector getPosition() {
        return position;
    }

    public void setPosition(D3Vector position) {
        this.position = position;
    }

    /**
     * Last known velocity of this drone in the architecture
     */
    private volatile D3Vector velocity = new D3Vector();

    public D3Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(D3Vector velocity) {
        this.velocity = velocity;
    }
    /**
     * Last known acceleration of this drone in the architecture
     */
    private volatile D3Vector acceleration = new D3Vector();

    public D3Vector getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(D3Vector acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * Last known direction of this drone in the architecture
     */
    private volatile D3PolarCoordinate direction = new D3PolarCoordinate();

    public D3PolarCoordinate getDirection() {
        return direction;
    }

    public void setDirection(D3PolarCoordinate direction) {
        this.direction = direction;
    }

    /**
     * Start the GPS (called from Apache Felix). This initializes to what messages the subscriber should listen.
     */
    public void start() {
        try {
            Thread.sleep(5000); // To ensure PubSubAdmin can give us a subscriber.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Subcriber initialization is now done by the Activator.
    }

    @Override
    public void receive(Object o, MultipartCallbacks multipartCallbacks) {
        System.out.println("[GPS] Got message " + o);
        if (o instanceof StateMessage && ((StateMessage) o).getIdentifier().equals(this.drone.getIdentifier())) {
            StateMessage message = (StateMessage) o;
            //Prepare some variables
            double deltaNow = ChronoUnit.MILLIS.between(message.getTimestamp(), LocalTime.now());
            Optional<D3Vector> optionalPosition = message.getPosition() == null ?
                    Optional.empty() : Optional.of(message.getPosition());
            Optional<D3Vector> optionalVelocity = message.getVelocity() == null ?
                    Optional.empty() : Optional.of(message.getVelocity());
            Optional<D3Vector> optionalAcceleration = message.getAcceleration() == null ?
                    Optional.empty() : Optional.of(message.getAcceleration());
            Optional<D3PolarCoordinate> optionalDirection = message.getDirection() == null ?
                    Optional.empty() : Optional.of(message.getDirection());

            //Check if the message is recent
            if (previousMessage != null && deltaNow > Settings.getTickTime(ChronoUnit.MILLIS)) {
                double deltaMessages = ChronoUnit.MILLIS.between(previousMessage.getTimestamp(), message.getTimestamp());
                if (deltaMessages <= 0) {
                    //We cannot use two messages that were send at the same time since this will create a NaN. To avoid getting this multiple times, we do not update the last message.
                    return;
                }
                interpolateMessages(deltaNow, deltaMessages, message);
            } else {
                optionalPosition.ifPresent(this::setPosition);
                optionalAcceleration.ifPresent(this::setAcceleration);
                optionalVelocity.ifPresent(this::setVelocity);
                optionalDirection.ifPresent(this::setDirection);
            }
            previousMessage = message;
            //Run callbacks
            callbacks.forEach(callback -> callback.run(message));
        }
    }

    /**
     * Interpolate the position, velocity and acceleration from the previous message and the current message. The estimated values are also set to the corresponding
     * fields. This method makes use of the other interpolate methods to achieve this.
     *
     * @param deltaNow      the difference between the moment the message was send and the current time.
     * @param deltaMessages the difference between the moment the previous message was send and the current message was send.
     * @param message       The current message
     */
    private void interpolateMessages(double deltaNow, double deltaMessages, StateMessage message) {
        Optional<D3Vector> optionalPosition = message.getPosition() == null ?
                Optional.empty() : Optional.of(message.getPosition());
        Optional<D3Vector> optionalVelocity = message.getVelocity() == null ?
                Optional.empty() : Optional.of(message.getVelocity());
        Optional<D3Vector> optionalAccelration = message.getAcceleration() == null ?
                Optional.empty() : Optional.of(message.getAcceleration());
        Optional<D3Vector> optionalPreviousPosition = previousMessage.getPosition() == null ?
                Optional.empty() : Optional.of(previousMessage.getPosition());
        Optional<D3Vector> optionalPreviousVelocity = previousMessage.getVelocity() == null ?
                Optional.empty() : Optional.of(previousMessage.getVelocity());
        Optional<D3Vector> optionalPreviousAcceleration = previousMessage.getAcceleration() == null ?
                Optional.empty() : Optional.of(previousMessage.getAcceleration());

        //Use the previous message to make a better guess of the location the drone probably is.
        if (optionalAccelration.isPresent() && optionalPreviousAcceleration.isPresent()) {
            D3Vector estimatedAcceleration = interpolateAcceleration(deltaNow, deltaMessages, optionalAccelration.get(), optionalPreviousAcceleration.get());

            if (optionalVelocity.isPresent() && optionalPreviousVelocity.isPresent()) {
                D3Vector estimatedVelocity = interpolateVelocity(deltaNow, deltaMessages, optionalVelocity.get(), optionalAccelration.get(), optionalPreviousVelocity.get(),
                        estimatedAcceleration);

                if (optionalPosition.isPresent() && optionalPreviousPosition.isPresent()) {
                    interpolatePosition(deltaNow, deltaMessages, optionalPosition.get(), optionalAccelration.get(), optionalPreviousPosition.get(), estimatedVelocity);
                }
            }
        }
    }

    private void interpolatePosition(double deltaNow, double deltaMessages, D3Vector optionalPosition, D3Vector optionalAccelration, D3Vector optionalPreviousPosition, D3Vector estimatedVelocity) {
        D3Vector deltaPosition;
        if (optionalAccelration.equals(D3Vector.ZERO)) {
            deltaPosition = D3Vector.ZERO;
        } else {
            deltaPosition = optionalPosition.normalize().scale(optionalPosition.sub(optionalPreviousPosition).length() / deltaMessages);
        }
        D3Vector estimatedPosition = optionalPosition.add(deltaPosition.scale(deltaNow / 1000)).add(estimatedVelocity.scale(Settings.getTickTime(ChronoUnit.SECONDS)));
        setPosition(estimatedPosition);
    }

    private D3Vector interpolateVelocity(double deltaNow, double deltaMessages, D3Vector optionalVelocity, D3Vector optionalAccelration, D3Vector optionalPreviousVelocity, D3Vector estimatedAcceleration) {
        D3Vector deltaVelocity;
        if (optionalAccelration.equals(D3Vector.ZERO)) {
            deltaVelocity = D3Vector.ZERO;
        } else {
            deltaVelocity = optionalVelocity.normalize().scale(optionalVelocity.sub(optionalPreviousVelocity).length() / deltaMessages);
        }
        D3Vector estimatedVelocity = optionalVelocity.add(deltaVelocity.scale(deltaNow / 1000)).add(estimatedAcceleration.scale(Settings.getTickTime(ChronoUnit.SECONDS)));
        setVelocity(estimatedVelocity);
        return estimatedVelocity;
    }

    private D3Vector interpolateAcceleration(double deltaNow, double deltaMessages, D3Vector acceleration, D3Vector previousAcceleration) {
        D3Vector deltaAcceleration;
        if (acceleration.equals(D3Vector.ZERO)) {
            deltaAcceleration = D3Vector.ZERO;
        } else {
            deltaAcceleration = acceleration.normalize().scale(acceleration.sub(previousAcceleration).length() / deltaMessages);
        }
        D3Vector estimatedAcceleration = acceleration.add(deltaAcceleration.scale(deltaNow / 1000));
        setAcceleration(estimatedAcceleration);
        return estimatedAcceleration;
    }

    /**
     * Submit a callback-function that is called after each state update is received. The StateMessage is a parameter for this callback.
     *
     * @param callback the function to be called
     */
    public final void registerCallback(GPSCallback callback) {
        callbacks.add(callback);
    }

    @FunctionalInterface
    public interface GPSCallback {
        void run(StateMessage newState);
    }
}
