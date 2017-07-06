package org.inaetics.dronessimulator.visualisation.uiupdates;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class RemoveDrone extends UIUpdate {
    /**
     * The image of the drone to remove
     */
    private final ImageView imageView;
    /**
     * The height text of the drone to remove
     */
    private final Text heightText;

    /**
     * Instantiates an update to remove a drone and the height text
     * @param imageView - The image view to remove. This is the image of the drone.
     * @param heightText - The heighttext to remove
     */
    public RemoveDrone(ImageView imageView, Text heightText) {
        this.imageView = imageView;
        this.heightText = heightText;
    }

        /**
         * Remove the drone image and the height text from a pane
         */
    @Override
    public void execute(Pane pane) {
        pane.getChildren().remove(imageView);
        pane.getChildren().remove(heightText);
    }
}
