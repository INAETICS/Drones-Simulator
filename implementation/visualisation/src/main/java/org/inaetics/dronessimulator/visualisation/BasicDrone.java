package org.inaetics.dronessimulator.visualisation;

import javafx.scene.layout.Pane;

public class BasicDrone extends Drone {

    private static final String image = "/drones/drone_sprite.png";

    /**
     * Creates a basic drone based on an image
     *
     * @param pane - Pane to add the drone to
     */
    public BasicDrone(Pane pane) {
        super(pane, image);
        init();
    }

    private void init() {

    }

}
