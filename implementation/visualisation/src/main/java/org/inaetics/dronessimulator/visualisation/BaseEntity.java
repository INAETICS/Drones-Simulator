package org.inaetics.dronessimulator.visualisation;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.inaetics.dronessimulator.common.D3PoolCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;

/**
 * Base class for each entity inside the visualitation
 * This class can be extended by e.g.: drones, bullets, gamified objects
 */
public abstract class BaseEntity {

    ImageView imageView;

    D3Vector position;
    private D3PoolCoordinate direction;

    private double spriteX;
    private double spriteY;
    private double spriteZ;

    Pane pane;

    /**
     * @param pane - Pane to add the basic entity to
     * @param image - String containing the path to an image to visualise the basic entity
     */
    BaseEntity(Pane pane, String image) {
        this.pane = pane;
        this.imageView = new ImageView(new Image(getClass().getResourceAsStream(image)));
        this.imageView.setPreserveRatio(true);
        this.pane.getChildren().addAll(this.imageView);
    }

    /**
     * Update the position, rotation and scale of the entity
     */
    void updateUI() {
        setSpriteX(position.getX() - imageView.getFitWidth() / 2);
        setSpriteY(position.getY() - imageView.getFitHeight() / 2);
        imageView.relocate(getSpriteX(), getSpriteY());
        imageView.setRotate(getRotation());
        imageView.setScaleX(getScale());
        imageView.setScaleY(getScale());
    }

    public void delete() {
        this.pane.getChildren().remove(this.imageView);
    }

    /**
     * Returns the x coordinate of the left upper corner of the entity's sprite
     * @return spriteX - x coordinate
     */
    public double getSpriteX() {
        return spriteX;
    }

    /**
     * Returns the y coordinate of the left upper corner of the entity's sprite
     * @return spriteY - y coordinate
     */
    public double getSpriteY() {
        return spriteY;
    }

    /**
     * Set the x-coordinate of the left upper corner of the entity's sprite
     * @param x - x coordinate
     */
    private void setSpriteX(double x) {
        spriteX = x;
    }

    /**
     * Set the y-coordinate of the left upper corner of the entity's sprite
     * @param y - y coordinate
     */
    private void setSpriteY(double y) {
        spriteY = y;
    }

    public double getScale() {
        return scale(position.getZ(), 0, 1000, 0.1, 1.0);
    }

    public void setPosition(D3Vector position) {
        this.position = position;
    }

    public void setDirection(D3PoolCoordinate direction) {
        this.direction = direction;
    }

    private double getRotation() {
        return this.direction.getAngle1Degrees();
    }

    /**
     * Scale a value from one scale to another.
     * Example: a double, 10 on a scale from 0 - 99 is scaled to a scale from 0-9. The return value would then be 1
     *
     * @param valueIn - Double to scale
     * @param baseMin - Lower bound of the original scale
     * @param baseMax - Upper bound of the original scale
     * @param limitMin - Lower bound of the new scale
     * @param limitMax - Upper bound of the new scale
     * @return - The double scaled to the new scale
     */
    public static double scale(final double valueIn, final double baseMin, final double baseMax, final double limitMin, final double limitMax) {
        return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
    }
}
