package org.inaetics.dronessimulator.visualisation;

import javafx.scene.layout.Pane;

/**
 * Created by langstra on 22-5-17.
 */
public class Bullet extends BaseEntity {

    private static final String image = "/bullet.png";

    public Bullet(Pane layer) {
        super(layer, image);
        this.imageView.setFitHeight(Settings.BULLET_HEIGHT);
        this.imageView.setId("bullet");
    }


}
