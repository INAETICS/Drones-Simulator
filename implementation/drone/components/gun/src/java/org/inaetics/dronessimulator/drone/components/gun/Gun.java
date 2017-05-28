package org.inaetics.dronessimulator.drone.components.gun;

import org.inaetics.dronessimulator.common.D3PoolCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.*;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;
import java.util.UUID;


public class Gun {
    private volatile Subscriber m_subscriber;
    private volatile Publisher m_publisher;
    private volatile DroneInit m_drone;
    private volatile GPS m_gps;
    private long last_shot_at_ms = System.currentTimeMillis();
    private final double GUN_SPEED = 400.0;
    private final double MAX_DISTANCE = 400;
    private final double SHOT_TIME_BETWEEN = 1000;

    public double getMaxDistance(){
        return MAX_DISTANCE;
    }




    public long msSinceLastShot(){ return System.currentTimeMillis() - this.last_shot_at_ms; }

    public void fireBullet(D3PoolCoordinate direction){
        if (this.msSinceLastShot() >= SHOT_TIME_BETWEEN){
            FireBulletMessage msg = new FireBulletMessage();
            msg.setDamage(100);
            msg.setFiredById(m_drone.getIdentifier());
            msg.setIdentifier(UUID.randomUUID().toString());
            msg.setType(EntityType.BULLET);
            msg.setDirection(direction);
            msg.setVelocity(direction.toVector().scale(GUN_SPEED / direction.toVector().length()));
            msg.setPosition(m_gps.getPosition());
            msg.setAcceleration(new D3Vector());

            this.last_shot_at_ms = System.currentTimeMillis();

            try{
                m_publisher.send(MessageTopic.MOVEMENTS, msg);
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
