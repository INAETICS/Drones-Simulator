package org.inaetics.dronessimulator.drone.components.gun;

import org.inaetics.dronessimulator.common.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.*;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;


public class Gun {
    private volatile Subscriber m_subscriber;
    private volatile Publisher m_publisher;
    private volatile DroneInit m_drone;
    private volatile GPS m_gps;
    private long last_shot_at_ms = System.currentTimeMillis();
    private long next_shot_at_ms = last_shot_at_ms;
    private final double GUN_SPEED = 150.0;
    private final double MAX_DISTANCE = 300;
    private final long BASE_SHOT_TIME_BETWEEN = 500;
    private final int MAX_OFFSET_SHOT_TIME = 1000;

    public double getMaxDistance(){
        return MAX_DISTANCE;
    }

    public long msSinceLastShot(){ return System.currentTimeMillis() - this.last_shot_at_ms; }

    public void fireBullet(D3PolarCoordinate direction){
        long current_time_ms = System.currentTimeMillis();
        if (current_time_ms >= next_shot_at_ms){
            FireBulletMessage msg = new FireBulletMessage();
            msg.setDamage(20);
            msg.setFiredById(m_drone.getIdentifier());
            msg.setIdentifier(UUID.randomUUID().toString());
            msg.setType(EntityType.BULLET);
            msg.setDirection(direction);
            msg.setVelocity(direction.toVector().scale(GUN_SPEED / direction.toVector().length()));
            msg.setPosition(m_gps.getPosition());
            msg.setAcceleration(new D3Vector());

            next_shot_at_ms = current_time_ms + BASE_SHOT_TIME_BETWEEN + new Random().nextInt(MAX_OFFSET_SHOT_TIME);

            try{
                m_publisher.send(MessageTopic.MOVEMENTS, msg);
            } catch(IOException e){
                e.printStackTrace();
            }

            System.out.println("FIRING BULLET!");
            System.out.println("Next shot possible in " + ((double) (next_shot_at_ms - current_time_ms) / 1000) + " seconds.");
        }
    }
}
