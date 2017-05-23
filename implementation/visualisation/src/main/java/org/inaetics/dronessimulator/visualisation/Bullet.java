package org.inaetics.dronessimulator.visualisation;

import javafx.scene.layout.Pane;

public class Bullet extends BaseEntity {

    private static final String image = "/bullet.png";

    /**
     * Creates a new bullet
     * All bullets are based on the same sprite
     *
     * @param pane - Pane to add the bullet to
     */
    public Bullet(Pane pane) {
        super(pane, image);
        this.imageView.setFitHeight(Settings.BULLET_HEIGHT);
        this.imageView.setId("bullet");
    }


}
