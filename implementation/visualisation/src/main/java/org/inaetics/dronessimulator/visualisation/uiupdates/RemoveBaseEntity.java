package org.inaetics.dronessimulator.visualisation.uiupdates;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class RemoveBaseEntity extends UIUpdate {
    private final ImageView imageView;

    public RemoveBaseEntity(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public void execute(Pane pane) {
        pane.getChildren().remove(this.imageView);
    }
}
