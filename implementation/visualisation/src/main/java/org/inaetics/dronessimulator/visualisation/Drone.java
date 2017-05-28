package org.inaetics.dronessimulator.visualisation;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.inaetics.dronessimulator.visualisation.uiupdates.AddDrone;
import org.inaetics.dronessimulator.visualisation.uiupdates.Explosion;
import org.inaetics.dronessimulator.visualisation.uiupdates.RemoveDrone;
import org.inaetics.dronessimulator.visualisation.uiupdates.UIUpdate;

import java.util.concurrent.BlockingQueue;

import static org.inaetics.dronessimulator.visualisation.Settings.DRONE_SPRITE_COLUMNS;

public abstract class Drone extends BaseEntity {

    private Text heightText;

    /**
     * Creates a drone based on a sprite
     *
     * @param pane - Pane to add the drone to
     * @param image - Path to the sprite image
     */
    Drone(BlockingQueue<UIUpdate> uiUpdates, Pane pane, String image) {
        super(uiUpdates, pane, image);
        new SpriteAnimation(imageView, Duration.millis(200), DRONE_SPRITE_COLUMNS, DRONE_SPRITE_COLUMNS, 0, 0, Settings.SPRITE_WIDTH, Settings.SPRITE_HEIGTH).play();
        heightText = new Text(0, 20, "Height: 0");
        heightText.setFill(Color.WHITE);
        imageView.setFitHeight(Settings.DRONE_HEIGHT);
        imageView.setFitWidth(Settings.DRONE_WIDTH);
        imageView.setId("drone");

        uiUpdates.add(new AddDrone(heightText));
    }

    /**
     * Updates the height text and calls the parent method
     */
    void updateUI() {
        super.updateUI();
        heightText.setText("Height: " + position.getZ());
        heightText.relocate(getSpriteX() + Settings.DRONE_WIDTH / 2 * (1 - getScale()), getSpriteY() + Settings.DRONE_HEIGHT / 2 * (1- getScale()) - 20);
    }

    /**
     * Removes the drone and initiates the explosion
     */
    @Override
    public void delete() {
        explode();
        getUiUpdates().add(new RemoveDrone(imageView));
    }

    private void explode() {
        getUiUpdates().add(new Explosion(getScale(), imageView));
    }
}
