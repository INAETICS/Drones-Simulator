package org.inaetics.dronessimulator.visualisation.uiupdates;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * RemoveBaseEntity extends UIUpdate and is a class representation used when removing a BaseEntity (drone, bullet, etc)
 */
public class RemoveBaseEntity extends UIUpdate {
    /**
     * The image of the base entity to remove
     */
    private final ImageView imageView;

    /**
     * Instantiates an update to remove a base entity
     * @param imageView
     */
    public RemoveBaseEntity(ImageView imageView) {
        this.imageView = imageView;
    }

    /**
     * Remove the base entity image from a pane
     */
    @Override
    public void execute(Pane pane) {
        pane.getChildren().remove(this.imageView);
    }
}
