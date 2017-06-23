package org.inaetics.dronessimulator.visualisation.uiupdates;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class AddBaseEntity extends UIUpdate {
    private final ImageView imageView;

    public AddBaseEntity(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public void execute(Pane pane) {
        pane.getChildren().addAll(this.imageView);
    }
}
