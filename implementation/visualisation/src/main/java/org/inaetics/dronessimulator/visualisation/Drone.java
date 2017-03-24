package org.inaetics.dronessimulator.visualisation;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.inaetics.isep.D3PoolCoordinate;
import org.inaetics.isep.D3Vector;

import static org.inaetics.dronessimulator.visualisation.Settings.DRONE_SPRITE_COLUMNS;

/**
 * Created by langstra on 10-3-17.
 */
public abstract class Drone {

    Image image;
    private ImageView imageView;

    private Input input;

    private D3Vector position;
    private D3PoolCoordinate direction;
    private D3Vector velocity;
    private D3Vector acceleration;

    private Pane layer;

    private boolean removable = false;

    private int width;
    private int height;
    private int offsetX = 0;
    private int offsetY = 0;
    private int count = DRONE_SPRITE_COLUMNS;
    private int columns = DRONE_SPRITE_COLUMNS;


    private SpriteAnimation animation;

    public Drone(Pane layer, String image, Input input) {
        this.input = input;

        this.layer = layer;
        this.image = new Image(getClass().getResourceAsStream(image));
        this.position = input.getPosition();
        this.direction = input.getDirection();
        this.velocity = input.getVelocity();
        this.acceleration = input.getAcceleration();

        this.width = Settings.SPRITE_WIDTH;
        this.height = Settings.SPRITE_HEIGTH;

        this.imageView = new ImageView(image);
        this.imageView.setPreserveRatio(true);
        this.imageView.setFitHeight(Settings.DRONE_HEIGTH);
        this.imageView.setViewport(new Rectangle2D(offsetX, offsetY, width, height));
        animation = new SpriteAnimation(imageView, Duration.millis(200), count, columns, offsetX, offsetY, width, height);
        animation.play();
        addToLayer();

    }

    public void addToLayer() {
        this.layer.getChildren().addAll(this.imageView);
    }

    public void removeFromLayer() {
        this.layer.getChildren().remove(this.imageView);
    }

    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    public void move() {

    }

    public ImageView getView() {
        return imageView;
    }

    public void updateUI() {

        imageView.relocate(position.getX(), position.getY());
        imageView.setRotate(getRotation());

    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getCenterX() {
        return position.getX() + width * 0.5;
    }

    public double getCenterY() {
        return position.getY() + height * 0.5;
    }

    public void processInput() {
        input.processInput();
        this.position = input.getPosition();
        this.direction = input.getDirection();
        this.velocity = input.getVelocity();
        this.acceleration = input.getAcceleration();
    }

    /**
     * Set flag that the sprite can be removed from the UI.
     */
    public void remove() {
        setRemovable(true);
    }

    public abstract void checkRemovability();

    private double getRotation() {
        return this.direction.getAngle1Degrees();
    }

}
