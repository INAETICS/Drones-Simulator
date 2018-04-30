package org.inaetics.drone.simulator.gameengine;

import org.inaetics.drone.simulator.spi.events.DroneAnnouncement;
import org.inaetics.pubsub.api.pubsub.Subscriber;
import org.osgi.service.log.LogService;

/**
 * Created by michiel on 20-4-18.
 */
public class GameEngine implements IGameEngine, Subscriber {
    private volatile LogService log;

    public void start () {
        System.out.println("LJKASDLKJAFSDJ");
    }

    @Override
    public void receive(Object o, MultipartCallbacks multipartCallbacks) {
        if (o instanceof DroneAnnouncement) {

        }
    }
}
