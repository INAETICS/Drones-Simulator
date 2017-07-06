package org.inaetics.dronessimulator.visualisation;

import javafx.scene.layout.Pane;
import org.inaetics.dronessimulator.visualisation.uiupdates.UIUpdate;

import java.util.concurrent.BlockingQueue;

public class Bullet extends BaseEntity {

    /** Image of the bullet */
    private static final String image = "/bullet.png";

    /**
     * Creates a new bullet
     * All bullets are based on the same sprite
     * @param uiUpdates - uiupdates
     */
    public Bullet(BlockingQueue<UIUpdate> uiUpdates) {
        super(uiUpdates, image);
        this.imageView.setFitHeight(Settings.BULLET_HEIGHT);
        this.imageView.setId("bullet");
    }


}
