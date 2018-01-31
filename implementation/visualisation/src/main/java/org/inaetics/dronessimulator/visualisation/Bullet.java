package org.inaetics.dronessimulator.visualisation;

import org.inaetics.dronessimulator.visualisation.uiupdates.UIUpdate;

import java.util.concurrent.BlockingQueue;

/**
 * The bullet class is a subclass of a base entity representing a bullet.
 */
public class Bullet extends BaseEntity {

    /** Image of the bullet */
    private static final String IMAGE = "/bullet.png";

    /**
     * Creates a new bullet
     * All bullets are based on the same sprite
     * @param uiUpdates - uiupdates
     */
    public Bullet(BlockingQueue<UIUpdate> uiUpdates) {
        super(uiUpdates, IMAGE);
        this.imageView.setFitHeight(Settings.BULLET_HEIGHT);
        this.imageView.setId("bullet");
    }
}
