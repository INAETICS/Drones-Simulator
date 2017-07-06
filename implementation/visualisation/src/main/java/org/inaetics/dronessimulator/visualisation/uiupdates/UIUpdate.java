package org.inaetics.dronessimulator.visualisation.uiupdates;

import javafx.scene.layout.Pane;

/**
 * The abstract ui update class. Any specific ui update class must extend this class.
 * This class is a wrapper that contains an update for the ui. The JavaFX application can keep a list of ui updates
 * and run the execute function of the ui update class.
 */
public abstract class UIUpdate {

    /**
     * Execute the update
     * @param pane The pane to execute the update on
     */
    public abstract void execute(Pane pane);
}
