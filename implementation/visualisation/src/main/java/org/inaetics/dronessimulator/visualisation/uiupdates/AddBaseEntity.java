package org.inaetics.dronessimulator.visualisation.uiupdates;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * AddBaseEntity extends UIUpdate and is a class representation used when adding a new BaseEntity (bullet)
 */
public class AddBaseEntity extends UIUpdate {
    /**
     * The image of the base entity to add
     */
    private final ImageView imageView;

    /**
     * Instantiates an update to add a base entity
     * @param imageView
     */
    public AddBaseEntity(ImageView imageView) {
        this.imageView = imageView;
    }

    /**
     * Add the base entity image to a pane
     */
    @Override
    public void execute(Pane pane) {
        pane.getChildren().addAll(this.imageView);
    }
}
