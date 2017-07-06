package org.inaetics.dronessimulator.visualisation;

import javafx.scene.layout.Pane;
import org.inaetics.dronessimulator.visualisation.uiupdates.UIUpdate;

import java.util.concurrent.BlockingQueue;

public class BasicDrone extends Drone {

    /** Image of the drone */
    private static final String IMAGE = "/drones/drone_sprite.png";

    /**
     * Instantiates a basic drone based on an image
     * @param uiUpdates - uiupdates
     */
    public BasicDrone(BlockingQueue<UIUpdate> uiUpdates) {
        super(uiUpdates, IMAGE);
    }
}
