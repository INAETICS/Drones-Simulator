package org.inaetics.dronessimulator.visualisation;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.inaetics.dronessimulator.common.D3PoolCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;

import static org.inaetics.dronessimulator.visualisation.Settings.DRONE_SPRITE_COLUMNS;

abstract class Drone {

    private ImageView imageView;
    private Text heightText;

    private Input input;

    private D3Vector position;
    private D3PoolCoordinate direction;
    private D3Vector velocity;
    private D3Vector acceleration;

    private Pane layer;

    private int width;

    Drone(Pane layer, String image, Input input) {
        this.input = input;

        this.layer = layer;

        this.width = Settings.DRONE_WIDTH;
        this.imageView = new ImageView(new Image(getClass().getResourceAsStream(image)));
        this.imageView.setPreserveRatio(true);
        this.imageView.setFitHeight(Settings.DRONE_HEIGTH);
        this.imageView.setViewport(new Rectangle2D(0, 0, Settings.SPRITE_WIDTH, Settings.SPRITE_HEIGTH));
        new SpriteAnimation(imageView, Duration.millis(200), DRONE_SPRITE_COLUMNS, DRONE_SPRITE_COLUMNS, 0, 0, Settings.SPRITE_WIDTH, Settings.SPRITE_HEIGTH).play();
        heightText = new Text(0, 20, "This is a text sample");
        addToLayer();
    }

    private void addToLayer() {
        this.layer.getChildren().addAll(this.imageView);
        this.layer.getChildren().addAll(this.heightText);
    }

    void updateUI() {

        imageView.relocate(position.getX(), position.getY());
        imageView.setRotate(getRotation());
        heightText.setText("Height: " + position.getZ());
        heightText.relocate(position.getX() + this.width, position.getY());

    }

    /**
     * For position, direction, velocity and acceleration process the input given by the publisher
     */
    void processInput() {
        this.position = input.getPosition();
        this.direction = input.getDirection();
//        this.velocity = input.getVelocity();
//        this.acceleration = input.getAcceleration();

    }

    private double getRotation() {
        return this.direction.getAngle1Degrees();
    }

}
