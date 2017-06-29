package org.inaetics.dronessimulator.visualisation;

import javafx.scene.layout.Pane;
import org.inaetics.dronessimulator.visualisation.uiupdates.UIUpdate;

import java.util.concurrent.BlockingQueue;

public class BasicDrone extends Drone {

    private static final String IMAGE = "/drones/drone_sprite.png";

    /**
     * Creates a basic drone based on an image
     *
     * @param pane - Pane to add the drone to
     */
    public BasicDrone(BlockingQueue<UIUpdate> uiUpdates, Pane pane) {
        super(uiUpdates, pane, IMAGE);
    }
}
