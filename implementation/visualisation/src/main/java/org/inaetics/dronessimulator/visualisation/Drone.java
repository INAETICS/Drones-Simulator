package org.inaetics.dronessimulator.visualisation;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Set;

import static org.inaetics.dronessimulator.visualisation.Settings.DRONE_SPRITE_COLUMNS;

abstract class Drone extends BaseEntity {

    private Text heightText;

    /**
     * Creates a drone based on a sprite
     *
     * @param pane - Pane to add the drone to
     * @param image - Path to the sprite image
     */
    Drone(Pane pane, String image) {
        super(pane, image);
        new SpriteAnimation(imageView, Duration.millis(200), DRONE_SPRITE_COLUMNS, DRONE_SPRITE_COLUMNS, 0, 0, Settings.SPRITE_WIDTH, Settings.SPRITE_HEIGTH).play();
        heightText = new Text(0, 20, "Height: 0");
        heightText.setFill(Color.WHITE);
        imageView.setFitHeight(Settings.DRONE_HEIGHT);
        imageView.setFitWidth(Settings.DRONE_WIDTH);
        imageView.setId("drone");
        pane.getChildren().addAll(heightText);
    }

    /**
     * Updates the height text and calls the parent method
     */
    void updateUI() {
        super.updateUI();
        heightText.setText("Height: " + position.getZ());
        heightText.relocate(getSpriteX() + Settings.DRONE_WIDTH / 2 * (1 - getScale()), getSpriteY() + Settings.DRONE_HEIGHT / 2 * (1- getScale()) - 20);
    }

    /**
     * Removes the drone and initiates the explosion
     */
    @Override
    public void delete() {
        explode();
        pane.getChildren().remove(imageView);
    }

    /**
     * Explosion animation
     * ImageView will be removed when the animation has finished
     */
    private void explode() {
        ImageView explosionImage = new ImageView(new Image(getClass().getResourceAsStream("/explosion.png")));
        explosionImage.setScaleX(getScale());
        explosionImage.setScaleY(getScale());
        explosionImage.setX(imageView.getLayoutX() - imageView.getFitWidth() / 2);
        explosionImage.setY(imageView.getLayoutY() - imageView.getFitHeight() / 2);
        SpriteAnimation explosionAnimation = new SpriteAnimation(explosionImage, Duration.millis(1000), 40, 8, 0, 0, 256, 256);
        explosionAnimation.setCycleCount(1);
        explosionAnimation.play();
        pane.getChildren().addAll(explosionImage);
        explosionAnimation.setOnFinished(
                new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Animation ended");
                        pane.getChildren().remove(explosionImage);
                    }
                }
        );
    }
}
