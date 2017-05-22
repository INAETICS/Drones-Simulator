package org.inaetics.dronessimulator.visualisation;

import javafx.scene.layout.Pane;

/**
 * Created by langstra on 10-3-17.
 */
public class BasicDrone extends Drone {

    double speed;
    double rotationSpeed;

    private static final String image = "/drones/drone_sprite.png";

    public BasicDrone(Pane layer) {
        super(layer, image);
        init();
    }


    private void init() {

    }

}
