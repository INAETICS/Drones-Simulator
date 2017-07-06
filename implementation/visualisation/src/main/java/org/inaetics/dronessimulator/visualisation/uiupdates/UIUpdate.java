package org.inaetics.dronessimulator.visualisation.uiupdates;

import javafx.scene.layout.Pane;

public abstract class UIUpdate {

    /**
     * Execute the update
     * @param pane The pane to execute the update on
     */
    public abstract void execute(Pane pane);
}
