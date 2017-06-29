package org.inaetics.dronessimulator.visualisation.uiupdates;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class RemoveDrone extends UIUpdate {
    public final ImageView imageView;
    public final Text heightText;

    /**
     * Instantiates an update to remove a drone and the height text
     * @param imageView
     * @param heightText
     */
    public RemoveDrone(ImageView imageView, Text heightText) {
        this.imageView = imageView;
        this.heightText = heightText;
    }

    @Override
    public void execute(Pane pane) {
        pane.getChildren().remove(imageView);
        pane.getChildren().remove(heightText);
    }
}
