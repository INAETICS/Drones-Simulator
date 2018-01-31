package org.inaetics.dronessimulator.visualisation;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
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
@Log4j
public abstract class BaseEntity {
    /**
     * List of ui updates it can add changes to the ui to
     */
    @Getter(AccessLevel.PROTECTED)
    private final BlockingQueue<UIUpdate> uiUpdates;
    /**
     * Image of the base entity
     */
    ImageView imageView;

    /**
     * Position of the base entity
     */
    @Setter
    D3Vector position;
    /**
     * Direction as a set of polar coordinates of the base entity
     */
    @Setter
    private D3PolarCoordinate direction;

    /*** X coordinate of the sprite, position of the upper left corner of the entity's sprite
     */
    @Getter
    @Setter
    private double spriteX;
    /*** Y coordinate of the sprite, position of the upper left corner of the entity's sprite
     */
    @Getter @Setter
    private double spriteY;

    @Getter
    @Setter
    private String id;

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
