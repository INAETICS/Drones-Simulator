package org.inaetics.dronessimulator.visualisation;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.inaetics.dronessimulator.visualisation.uiupdates.AddDrone;
import org.inaetics.dronessimulator.visualisation.uiupdates.Explosion;
import org.inaetics.dronessimulator.visualisation.uiupdates.RemoveDrone;
import org.inaetics.dronessimulator.visualisation.uiupdates.UIUpdate;

import java.util.concurrent.BlockingQueue;

import static org.inaetics.dronessimulator.visualisation.Settings.DRONE_SPRITE_COLUMNS;

/**
 * The drone class is a subclass of a base entity representing the most general of a drone. It should be extended by a
 * more class of a drone, which has an image that determine the view of the drone.
 */
public abstract class Drone extends BaseEntity {
    /** Current hitpoints of a drone */
    private int currentHP;
    /** Text attribute containing the height */
    private final Text heightText;

    /**
     * Creates a drone based on a sprite
     * @param uiUpdates - uiupdates
     * @param image - Path to the sprite image
     */
    Drone(BlockingQueue<UIUpdate> uiUpdates, String image) {
        super(uiUpdates, image);
        new SpriteAnimation(imageView, Duration.millis(200), DRONE_SPRITE_COLUMNS, DRONE_SPRITE_COLUMNS, 0, 0, Settings.SPRITE_WIDTH, Settings.SPRITE_HEIGTH).play();
        heightText = new Text(0, 20, "Height: 0");
        heightText.setFill(Color.WHITE);
        imageView.setFitHeight(Settings.DRONE_HEIGHT);
        imageView.setFitWidth(Settings.DRONE_WIDTH);
        imageView.setId("drone");
        currentHP = -1;
        uiUpdates.add(new AddDrone(heightText));
    }

    /**
     * Updates the height text and calls the parent method
     */
    void updateUI() {
        super.updateUI();
        heightText.setText("HP: " + currentHP + "/100 Location: " + position.toString(2));
        heightText.relocate(getSpriteX() + Settings.DRONE_WIDTH / 2.0 * (1 - getScale()), getSpriteY() + Settings.DRONE_HEIGHT / 2.0 * (1 - getScale()) - 20);
    }

    /**
     * Removes the drone and initiates the explosion
     * Adds an uiupdate telling the drone to be removed
     */
    @Override
    public void delete() {
        explode();
        getUiUpdates().add(new RemoveDrone(imageView, heightText));
    }

    /**
     * Adds an update to uiupdates telling the drone to explode
     */
    private void explode() {
        getUiUpdates().add(new Explosion(getScale(), imageView));
    }

    /**
     * Get the current hitpoints
     * @return - currentHP
     */
    public int getCurrentHP() {
        return currentHP;
    }

    /**
     * Set the number of hitpoints
     * @param currentHP - number of hitpoints
     */
    public void setCurrentHP(int currentHP) {
        this.currentHP = currentHP;
    }
}
