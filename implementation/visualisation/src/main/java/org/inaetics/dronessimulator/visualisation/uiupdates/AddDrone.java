package org.inaetics.dronessimulator.visualisation.uiupdates;

import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;

/**
 * AddDrone extends UIUpdate and is a class representation used when adding a new Drone
 * It adds the height text of the drone and the drone image
 */
@RequiredArgsConstructor
public class AddDrone extends UIUpdate {
    /**
     * Height text to add together with the drone
     */
    private final Text heightText;

    /**
     * Add the drone height text to a pane
     */
    @Override
    public void execute(Pane pane) {
        pane.getChildren().addAll(heightText);
    }
}
