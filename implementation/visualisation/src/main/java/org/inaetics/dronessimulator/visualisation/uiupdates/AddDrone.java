package org.inaetics.dronessimulator.visualisation.uiupdates;

import javafx.scene.layout.Pane;
import javafx.scene.text.Text;


public class AddDrone extends UIUpdate {
    private final Text heightText;

    /**
     * Instantiates an update to add a drone
     * Height text is the only thing that is specific for a drone, the drone itself it a base entity
     * @param heightText
     */
    public AddDrone(Text heightText) {
        this.heightText = heightText;
    }

    @Override
    public void execute(Pane pane) {
        pane.getChildren().addAll(heightText);
    }
}
