package org.inaetics.dronessimulator.visualisation;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.visualisation.uiupdates.AddBaseEntity;
import org.inaetics.dronessimulator.visualisation.uiupdates.RemoveBaseEntity;
import org.inaetics.dronessimulator.visualisation.uiupdates.UIUpdate;

import java.util.concurrent.BlockingQueue;

/**
 * Base class for each entity inside the visualisation
 * This class can be extended by e.g.: drones, bullets, gamified objects
 */
public abstract class BaseEntity {
    /** List of ui updates it can add changes to the ui to */
    private final BlockingQueue<UIUpdate> uiUpdates;
    /** Image of the base entity */
    ImageView imageView;

    /** Position of the base entity */
    D3Vector position;
    /** Direction as a set of polar coordinates of the base entity */
    private D3PolarCoordinate direction;

    /** X coordinate of the sprite, position of the upper left corner */
    private double spriteX;
    /** Y coordinate of the sprite, position of the upper left corner */
    private double spriteY;

    /**
     * Instantiates a new base entity
     * @param uiUpdates - UI updates shared by the system
     * @param image - String containing the path to an image to visualise the basic entity
     */
    BaseEntity(BlockingQueue<UIUpdate> uiUpdates, String image) {
        this.uiUpdates = uiUpdates;
        this.imageView = new ImageView(new Image(getClass().getResourceAsStream(image)));
        this.imageView.setPreserveRatio(true);

        this.uiUpdates.add(new AddBaseEntity(imageView));
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

    /**
     * Removes the base entity by adding a remove entity to the ui updates
     */
    public void delete() {
        uiUpdates.add(new RemoveBaseEntity(imageView));
    }

    /**
     * Returns the x coordinate of the left upper corner of the entity's sprite
     * @return spriteX - x coordinate
     */
    double getSpriteX() {
        return spriteX;
    }

    /**
     * Returns the y coordinate of the left upper corner of the entity's sprite
     * @return spriteY - y coordinate
     */
    double getSpriteY() {
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

    /**
     * Scales the height from 0-1000 to a double between 0.1 and 1.0
     * @return Double between 0.1 and 1.0
     */
    double getScale() {
        return scale(position.getZ(), 0, 1000, 0.1, 1.0);
    }

    /**
     * Set the position of the base entity
     * @param position - position
     */
    public void setPosition(D3Vector position) {
        this.position = position;
    }

    /**
     * Set the direction of the base entity
     * @param direction - direction
     */
    public void setDirection(D3PolarCoordinate direction) {
        this.direction = direction;
    }

    /**
     * Get the rotation in degrees
     * @return - rotation in degrees
     */
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
    private static double scale(final double valueIn, final double baseMin, final double baseMax, final double limitMin, final double limitMax) {
        return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
    }

    /**
     * Get the ui updates
     * @return - uiupdates
     */
    BlockingQueue<UIUpdate> getUiUpdates() {
        return uiUpdates;
    }
}
