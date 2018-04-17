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
    /**
     * List of ui updates it can add changes to the ui to
     */

    private final BlockingQueue<UIUpdate> uiUpdates;

    protected BlockingQueue<UIUpdate> getUiUpdates() {
        return uiUpdates;
    }

    /**
     * Image of the base entity
     */
    ImageView imageView;

    /**
     * Position of the base entity
     */
    D3Vector position;
    public void setPosition(D3Vector position) {
        this.position = position;
    }

    /**
     * Direction as a set of polar coordinates of the base entity
     */
    private D3PolarCoordinate direction;

    public void setDirection(D3PolarCoordinate direction) {
        this.direction = direction;
    }

    /*** X coordinate of the sprite, position of the upper left corner of the entity's sprite
     */
    private double spriteX;

    public double getSpriteX() {
        return spriteX;
    }

    public void setSpriteX(double spriteX) {
        this.spriteX = spriteX;
    }

    /*** Y coordinate of the sprite, position of the upper left corner of the entity's sprite
     */
    private double spriteY;

    public double getSpriteY() {
        return spriteY;
    }

    public void setSpriteY(double spriteY) {
        this.spriteY = spriteY;
    }

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Create the logger
     */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BaseEntity.class);

    /**
     * Instantiates a new base entity
     *
     * @param uiUpdates - UI updates shared by the system
     * @param image     - String containing the path to an image to visualise the basic entity
     */
    BaseEntity(BlockingQueue<UIUpdate> uiUpdates, String image) {
        this.uiUpdates = uiUpdates;
        this.imageView = new ImageView(new Image(getClass().getResourceAsStream(image)));
        this.imageView.setPreserveRatio(true);

        this.uiUpdates.add(new AddBaseEntity(imageView));
    }

    /**
     * Scale a value from one scale to another.
     * Example: a double, 10 on a scale from 0 - 99 is scaled to a scale from 0-9. The return value would then be 1
     *
     * @param valueIn  - Double to scale
     * @param baseMin  - Lower bound of the original scale
     * @param baseMax  - Upper bound of the original scale
     * @param limitMin - Lower bound of the new scale
     * @param limitMax - Upper bound of the new scale
     * @return - The double scaled to the new scale
     */
    private static double scale(final double valueIn, final double baseMin, final double baseMax, final double limitMin, final double limitMax) {
        return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
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
     * Scales the height from 0-1000 to a double between 0.1 and 1.0
     *
     * @return Double between 0.1 and 1.0
     */
    double getScale() {
        return scale(position.getZ(), 0, 1000, 0.1, 1.0); //TODO replace baseMax with an actual maximal value for this axis, no magic numbers!
    }

    /**
     * Get the rotation in degrees
     *
     * @return - rotation in degrees
     */
    private double getRotation() {
        return this.direction.getAngle1Degrees();
    }

}
