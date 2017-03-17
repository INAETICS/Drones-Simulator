package org.inaetics.dronessimulator.visualisation;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Created by langstra on 10-3-17.
 */
public abstract class Drone {

    Image image;
    private ImageView imageView;

    private Input input;

    private D3Vector position;
    private D3Vector direction;
    private D3Vector velocity;
    private D3Vector acceleration;

    private Pane layer;

    private boolean removable = false;

    private double w;
    private double h;

    public Drone(Pane layer, Image image, Input input) {
        this.input = input;

        this.layer = layer;
        this.image = image;
        this.position = input.getPosition();
        this.direction = input.getDirection();
        this.velocity = input.getVelocity();
        this.acceleration = input.getAcceleration();

        this.imageView = new ImageView(image);
        this.imageView.relocate(this.position.getX(), this.position.getY());
        this.imageView.setRotate(this.getRotation());
        this.imageView.setFitHeight(50);
        this.imageView.setFitWidth(50);

        this.w = 50; // imageView.getBoundsInParent().getWidth(); image.getWidth()
        this.h = 50; // imageView.getBoundsInParent().getHeight(); image.getHeight()

        addToLayer();

    }

    public void addToLayer() {
        this.layer.getChildren().add(this.imageView);
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

        imageView.relocate(x, y);
        imageView.setRotate(r);

    }

    public double getWidth() {
        return w;
    }

    public double getHeight() {
        return h;
    }

    public double getCenterX() {
        return x + w * 0.5;
    }

    public double getCenterY() {
        return y + h * 0.5;
    }

    /**
     * Set flag that the sprite can be removed from the UI.
     */
    public void remove() {
        setRemovable(true);
    }

    public abstract void checkRemovability();

    private double getRotation() {
        double rotation = Math.atan2(this.direction.getY(), this.direction.getX()); //1st qudrant
        if (this.direction.getX() < 0 && this.direction.getY() < 0) rotation += 90; //2end quadrant
        else if (this.direction.getX() < 0 && this.direction.getY() > 0) rotation += 180; //3rd quadrant
        else if (this.direction.getX() > 0 && this.direction.getY() > 0) rotation += 270; //4rd quadrant
        return rotation;
    }

}
