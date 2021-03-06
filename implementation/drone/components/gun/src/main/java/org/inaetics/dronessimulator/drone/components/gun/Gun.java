package org.inaetics.dronessimulator.drone.components.gun;

import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.protocol.FireBulletMessage;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.pubsub.api.pubsub.Publisher;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * The gun component for drone tactics
 */
public class Gun {
    //OSGi constructor
    public Gun() {
    }

    //Testing constructor
    public Gun(Publisher publisher, DroneInit drone, GPS gps, long lastShotAtMs, long nextShotAtMs) {
        this.publisher = publisher;
        this.drone = drone;
        this.gps = gps;
        this.lastShotAtMs = lastShotAtMs;
        this.nextShotAtMs = nextShotAtMs;
    }

    /**
     * The speed of the bullet
     */
    protected static final double BULLET_SPEED = 150.0;
    /**
     * The maximum distance the gun can aim
     */
    protected static final double MAX_DISTANCE = 1024;
    /**
     * Lowest time between shots
     */
    private static final long BASE_SHOT_TIME_BETWEEN = 500;
    /**
     * Maximum time added to {@link #BASE_SHOT_TIME_BETWEEN}
     */
    private static final int MAX_OFFSET_SHOT_TIME = 1000;
    private final Set<GunCallback> callbacks = new HashSet<>();
    /**
     * The Publisher to use for sending messages
     */
    private volatile Publisher publisher;
    /**
     * The drone instance that can be used to get information about the current drone
     */
    private volatile DroneInit drone;
    /**
     * The GPS that can be used to get the current position, velocity and acceleration
     */
    private volatile GPS gps;
    /**
     * Last time the gun has fired
     */
    private long lastShotAtMs = System.currentTimeMillis();
    /**
     * Next time the gun may fire
     */
    private long nextShotAtMs = lastShotAtMs;

    /**
     * Gives the fire range of the gun.
     *
     * @return the distance in m
     */
    public double getMaxDistance() {
        return MAX_DISTANCE;
    }

    /**
     * Gives the number of MilliSeconds scince the last shot is fired.
     */
    public long msSinceLastShot() {
        return System.currentTimeMillis() - this.lastShotAtMs;
    }

    /**
     * Create the logger
     */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Gun.class);

    /**
     * Fires a bullet in de given direction. If you know the position where the bullet should end, you can use the following code to call this method:
     * {@code gun.fireBullet(targetLocation.sub(gps.getPosition()).toPoolCoordinate());}
     *
     * @param direction in which the bullet must be fired.
     */
    public void fireBullet(D3PolarCoordinate direction) {
        long currentTimeMs = System.currentTimeMillis();

        if (currentTimeMs >= nextShotAtMs && direction.getLength() <= MAX_DISTANCE) {
            FireBulletMessage msg = new FireBulletMessage();
            msg.setDamage(20);
            msg.setFiredById(drone.getIdentifier());
            msg.setIdentifier(UUID.randomUUID().toString());
            msg.setType(EntityType.BULLET);
            msg.setDirection(direction);
            msg.setVelocity(direction.toVector().scale(BULLET_SPEED / direction.toVector().length()));
            msg.setPosition(gps.getPosition());
            msg.setAcceleration(new D3Vector());

            publisher.send(msg);
            lastShotAtMs = currentTimeMs;
            nextShotAtMs = lastShotAtMs + BASE_SHOT_TIME_BETWEEN + new Random().nextInt(MAX_OFFSET_SHOT_TIME);
            //Run all the callbacks
            callbacks.forEach(callback -> callback.run(msg));

            log.info("Firing bullet in direction " + direction + "! Next shot possible in " + ((double) (nextShotAtMs - currentTimeMs) / 1000) + " seconds.");
        }
    }

    /**
     * Submit a callback-function that is called after each bullet is fired. The FireBulletMessage is a parameter for this callback.
     *
     * @param callback the function to be called
     */
    public final void registerCallback(GunCallback callback) {
        callbacks.add(callback);
    }

    @FunctionalInterface
    public interface GunCallback {
        void run(FireBulletMessage fireBulletMessage);
    }
}
