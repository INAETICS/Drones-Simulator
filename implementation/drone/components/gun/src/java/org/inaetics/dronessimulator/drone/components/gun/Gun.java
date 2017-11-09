package org.inaetics.dronessimulator.drone.components.gun;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.protocol.FireBulletMessage;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

/**
 * The gun component for drone tactics
 */
public class Gun {
    /** The logger */
    private static final Logger logger = Logger.getLogger(Gun.class);

    /**
     * Reference to the Subscriber bundle
     */
    private volatile Subscriber m_subscriber;

    /**
     * Reference to the Publisher bundle
     */
    private volatile Publisher m_publisher;

    /**
     * Reference to the Drone Init bundle
     */
    private volatile DroneInit m_drone;

    /**
     * Reference to the GPS bundle
     */
    private volatile GPS m_gps;

    /**
     * Last time the gun has fired
     */
    private long last_shot_at_ms = System.currentTimeMillis();

    /**
     * Next time the gun may fire
     */
    private long next_shot_at_ms = last_shot_at_ms;

    /**
     * The speed of the bullet
     */
    private final double BULLET_SPEED = 150.0;

    /**
     * The maximum distance the gun can aim
     */
    private final double MAX_DISTANCE = 1024;

    /**
     * Lowest time between shots
     */
    private final long BASE_SHOT_TIME_BETWEEN = 500;

    /**
     * Maximum time added to {@link BASE_SHOT_TIME_BETWEEN}
     */
    private final int MAX_OFFSET_SHOT_TIME = 1000;

    // -- GETTERS

    /**
     * Gives the fire range of the gun.
     * @return the distance in m
     */
    public double getMaxDistance(){
        return MAX_DISTANCE;
    }

    /**
     * Gives the number of MilliSeconds scince the last shot is fired.
     * @return
     */
    public long msSinceLastShot(){
        return System.currentTimeMillis() - this.last_shot_at_ms;
    }

    // -- FUNCTIONS
    /**
     * Fires a bullet in de given direction.
     * @param direction in which the bullet must be fired.
     */
    public void fireBullet(D3PolarCoordinate direction){
        long current_time_ms = System.currentTimeMillis();

        if (current_time_ms >= next_shot_at_ms){
            FireBulletMessage msg = new FireBulletMessage();
            msg.setDamage(20);
            msg.setFiredById(m_drone.getIdentifier());
            msg.setIdentifier(UUID.randomUUID().toString());
            msg.setType(EntityType.BULLET);
            msg.setDirection(direction);
            msg.setVelocity(direction.toVector().scale(BULLET_SPEED / direction.toVector().length()));
            msg.setPosition(m_gps.getPosition());
            msg.setAcceleration(new D3Vector());

            next_shot_at_ms = current_time_ms + BASE_SHOT_TIME_BETWEEN + new Random().nextInt(MAX_OFFSET_SHOT_TIME);

            try{
                m_publisher.send(MessageTopic.MOVEMENTS, msg);
            } catch(IOException e){
                logger.fatal(e);
            }

            Logger.getLogger(Gun.class).info("Firing bullet! Next shot possible in " + ((double) (next_shot_at_ms - current_time_ms) / 1000) + " seconds.");
        }
    }
}
