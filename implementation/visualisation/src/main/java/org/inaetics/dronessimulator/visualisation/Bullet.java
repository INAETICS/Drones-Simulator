package org.inaetics.dronessimulator.visualisation;

import javafx.scene.layout.Pane;
import org.inaetics.dronessimulator.visualisation.uiupdates.UIUpdate;

import java.util.concurrent.BlockingQueue;

public class Bullet extends BaseEntity {

    private static final String image = "/bullet.png";

    /**
     * Creates a new bullet
     * All bullets are based on the same sprite
     *
     * @param pane - Pane to add the bullet to
     */
    public Bullet(BlockingQueue<UIUpdate> uiUpdates, Pane pane) {
        super(uiUpdates, pane, image);
        this.imageView.setFitHeight(Settings.BULLET_HEIGHT);
        this.imageView.setId("bullet");
    }


}
