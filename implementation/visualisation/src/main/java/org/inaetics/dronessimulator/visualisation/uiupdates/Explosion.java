package org.inaetics.dronessimulator.visualisation.uiupdates;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.visualisation.SpriteAnimation;

/**
 * Explosion extends UIUpdate and is a class representation used when adding a new explosion
 */
@Log4j
public class Explosion extends UIUpdate {
    /**
     * Scale of the explosion, regulates the size
     */
    private final double scale;
    /**
     * Image of the explosion
     */
    private final ImageView imageView;

    /**
     * Instantiates an update to add an explosion
     * @param imageView
     */
    public Explosion(double scale, ImageView imageView) {
        this.scale = scale;
        this.imageView = imageView;
    }

    /**
     * Add an explosion to a pane
     * Creates an explosion from an image and animates it.
     * The explosion removes itself after the animation finishes
     */
    @Override
    public void execute(Pane pane) {
        ImageView explosionImage = new ImageView(new Image(getClass().getResourceAsStream("/explosion.png")));
        explosionImage.setScaleX(scale);
        explosionImage.setScaleY(scale);
        explosionImage.setX(imageView.getLayoutX() - imageView.getFitWidth() / 2);
        explosionImage.setY(imageView.getLayoutY() - imageView.getFitHeight() / 2);
        SpriteAnimation explosionAnimation = new SpriteAnimation(explosionImage, Duration.millis(1000), 40, 8, 0, 0, 256, 256);
        explosionAnimation.setCycleCount(1);
        explosionAnimation.play();
        pane.getChildren().addAll(explosionImage);

        explosionAnimation.setOnFinished(
                event -> {
                    pane.getChildren().remove(explosionImage);
                    log.warn("Removed");
                }
        );
    }
}
