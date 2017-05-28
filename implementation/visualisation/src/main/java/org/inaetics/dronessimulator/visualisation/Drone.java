package org.inaetics.dronessimulator.visualisation;

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

public abstract class Drone {

    private final ImageView imageView;
    private final Text heightText;
    private final Pane layer;

    private final int width;
    private D3Vector position;
    private D3PoolCoordinate direction;

    private volatile boolean initialized;

    Drone(Pane layer, String image) {
        this.layer = layer;

        this.width = Settings.DRONE_WIDTH;
        this.imageView = new ImageView(new Image(getClass().getResourceAsStream(image)));
        this.imageView.setId("drone");
        this.imageView.setPreserveRatio(true);
        this.imageView.setFitHeight(Settings.DRONE_HEIGTH);
        this.imageView.setViewport(new Rectangle2D(0, 0, Settings.SPRITE_WIDTH, Settings.SPRITE_HEIGTH));
        new SpriteAnimation(imageView, Duration.millis(200), DRONE_SPRITE_COLUMNS, DRONE_SPRITE_COLUMNS, 0, 0, Settings.SPRITE_WIDTH, Settings.SPRITE_HEIGTH).play();
        heightText = new Text(0, 20, "Height: 0");
        heightText.setFill(Color.WHITE);

        initialized = false;
    }

    private void addToLayer() {
        this.layer.getChildren().addAll(this.imageView);
        this.layer.getChildren().addAll(this.heightText);
    }

    // Is run by javaFX
    void updateUI() {
        if(!initialized) {
            initialized = true;
            this.addToLayer();
        }

        imageView.relocate(position.getX(), position.getY());
        imageView.setRotate(getRotation());
        heightText.setText("Height: " + position.getZ());
        heightText.relocate(position.getX() + this.width, position.getY());

    }

    // Is run by SubscriberMessageHandler
    public void setPosition(D3Vector position) {
        this.position = position;
    }

    // Is run by SubscriberMessageHandler
    public void setDirection(D3PoolCoordinate direction) {
        this.direction = direction;
    }

    // Is run by SubscriberMessageHandler
    private double getRotation() {
        return this.direction.getAngle1Degrees();
    }

}
