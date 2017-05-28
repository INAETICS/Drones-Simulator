package org.inaetics.dronessimulator.visualisation.uiupdates;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class RemoveDrone extends UIUpdate {
    public final ImageView imageView;

    public RemoveDrone(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public void execute(Pane pane) {
        pane.getChildren().remove(imageView);
    }
}
