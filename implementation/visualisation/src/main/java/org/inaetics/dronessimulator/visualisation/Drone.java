package org.inaetics.dronessimulator.visualisation;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.inaetics.dronessimulator.common.D3PoolCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;

import static org.inaetics.dronessimulator.visualisation.Settings.DRONE_SPRITE_COLUMNS;

abstract class Drone extends BaseEntity {

    private Text heightText;

    Drone(Pane layer, String image) {
        super(layer, image);
        new SpriteAnimation(imageView, Duration.millis(200), DRONE_SPRITE_COLUMNS, DRONE_SPRITE_COLUMNS, 0, 0, Settings.SPRITE_WIDTH, Settings.SPRITE_HEIGTH).play();
        heightText = new Text(0, 20, "Height: 0");
        heightText.setFill(Color.WHITE);
        this.imageView.setFitHeight(Settings.DRONE_HEIGHT);
        this.imageView.setFitWidth(Settings.DRONE_WIDTH);
        this.imageView.setId("drone");
        this.layer.getChildren().addAll(this.heightText);
    }

    void updateUI() {
        super.updateUI();
        heightText.setText("Height: " + position.getZ());
        heightText.relocate(position.getX() + this.width, position.getY());
    }

    void delete() {
        explode();
        this.layer.getChildren().remove(this.imageView);
    }

    private void explode() {
        ImageView explosionImage = new ImageView(new Image(getClass().getResourceAsStream("/explosion.png")));
        explosionImage.setScaleX(getScale());
        explosionImage.setScaleY(getScale());
        explosionImage.setX(imageView.getLayoutX() - imageView.getFitWidth() / 2);
        explosionImage.setY(imageView.getLayoutY() - imageView.getFitHeight() / 2);
        SpriteAnimation explosionAnimation = new SpriteAnimation(explosionImage, Duration.millis(1000), 40, 8, 0, 0, 256, 256);
        explosionAnimation.setCycleCount(1);
        explosionAnimation.play();
        this.layer.getChildren().addAll(explosionImage);
        explosionAnimation.setOnFinished(
                new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Animation ended");
                        layer.getChildren().remove(explosionImage);
                    }
                }
        );
    }
}
