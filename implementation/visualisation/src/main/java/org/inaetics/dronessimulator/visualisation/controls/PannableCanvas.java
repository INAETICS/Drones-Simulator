package org.inaetics.dronessimulator.visualisation.controls;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class PannableCanvas extends Pane {
    /** Scale of the canvas */
    private DoubleProperty myScale = new SimpleDoubleProperty(1.0);

    /**
     * Instantiates a new pannable and zoomable canvas
     * @param width - width of the canvas in pixels
     * @param height - height of the canvas in pixels
     */
    public PannableCanvas(double width, double height) {
        setPrefSize(width, height);

        // add scale transform
        scaleXProperty().bind(myScale);
        scaleYProperty().bind(myScale);
    }

    /**
     * Add a grid to the canvas, send it to the back
     */
    public void addGrid() {
        double w = getBoundsInLocal().getWidth();
        double h = getBoundsInLocal().getHeight();

        // add grid
        Canvas grid = new Canvas(w, h);

        // don't catch mouse events
        grid.setMouseTransparent(true);

        GraphicsContext gc = grid.getGraphicsContext2D();

        gc.setStroke(Color.GRAY);
        gc.setLineWidth(1);

        // draw grid lines
        double offset = 50;
        for (double i = offset; i < w; i += offset) {
            gc.strokeLine(i, 0, i, h);
            gc.strokeLine(0, i, w, i);
        }

        getChildren().add(grid);

        grid.toBack();
    }

    /**
     * Get the scale of the canvas
     * @return - scale
     */
    double getScale() {
        return myScale.get();
    }

    /**
     * Set the scale of the canvas
     * @param scale - scale
     */
    void setScale(double scale) {
        myScale.set(scale);
    }

    /**
     * Set the x and y coordinates when zooming
     * @param x - x
     * @param y - y
     */
    void setPivot(double x, double y) {
        setTranslateX(getTranslateX() - x);
        setTranslateY(getTranslateY() - y);
    }
}
