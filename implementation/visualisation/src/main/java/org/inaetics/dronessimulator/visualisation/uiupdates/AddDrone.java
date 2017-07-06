package org.inaetics.dronessimulator.visualisation.uiupdates;

import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * AddDrone extends UIUpdate and is a class representation used when adding a new Drone
 * It adds the height text of the drone and the drone image
 */
public class AddDrone extends UIUpdate {
    /**
     * Height text to add together with the drone
     */
    private final Text heightText;

    /**
     * Instantiates an update to add a drone
     * Height text is the only thing that is specific for a drone, the drone itself it a base entity
     * @param heightText - The new initial height text shown above the drone
     */
    public AddDrone(Text heightText) {
        this.heightText = heightText;
    }

    /**
     * Add the drone height text to a pane
     */
    @Override
    public void execute(Pane pane) {
        pane.getChildren().addAll(heightText);
    }
}
