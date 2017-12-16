package org.inaetics.dronessimulator.visualisation.controls;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * Listeners for making the scene's canvas draggable and zoomable
 */
public class SceneGestures {

    /** Maximum zoom */
    private static final double MAX_SCALE = 15.0d;
    /** Miminal zoom */
    private static final double MIN_SCALE = 1.0d;

    /** Mouse drag context */
    private final DragContext sceneDragContext = new DragContext();

    /** Pannable and zommable canvas */
    private final PannableCanvas canvas;

    /** Handlers for the mouse events */
    public SceneGestures(PannableCanvas canvas) {
        this.canvas = canvas;
    }

    /**
     * Get the on mouse pressed event handler
     * @return - handler
     */
    public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }

    /**
     * Get the on mouse dragged event handler
     * @return - handler
     */
    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

    /**
     * Get the on mouse scroll event handler
     * @return - handler
     */
    public EventHandler<ScrollEvent> getOnScrollEventHandler() {
        return onScrollEventHandler;
    }

    /**
     * The on mouse pressed event handler
     */
    private final EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

        /**
         * Sets the current position of the mouse and the current position on the canvas
         * @param event - event
         */
        public void handle(MouseEvent event) {
            // right mouse button => panning
            if (!event.isSecondaryButtonDown()) {
                return;
            }

            sceneDragContext.mouseAnchorX = event.getSceneX();
            sceneDragContext.mouseAnchorY = event.getSceneY();

            sceneDragContext.translateAnchorX = canvas.getTranslateX();
            sceneDragContext.translateAnchorY = canvas.getTranslateY();
        }
    };

    /**
     * The on mouse dragged event handler
     */
    private final EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

        /**
         * Moves the canvas within
         * @param event - event
         */
        public void handle(MouseEvent event) {
            // right mouse button => panning
            if (!event.isSecondaryButtonDown()) {
                return;
            }

            double newX = sceneDragContext.translateAnchorX + event.getSceneX() - sceneDragContext.mouseAnchorX;
            double newY = sceneDragContext.translateAnchorY + event.getSceneY() - sceneDragContext.mouseAnchorY;

            canvas.setTranslateX(clamp(newX, minX(), maxX()));
            canvas.setTranslateY(clamp(newY, minY(), maxY()));

            event.consume();
        }
    };

    /**
     * Mouse wheel handler: zoom to pivot point
     */
    private final EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

        /**
         * Zooms the canvas to the mouse pointer
         * @param event
         */
        @Override
        public void handle(ScrollEvent event) {
            double delta = 1.2;
            double scale = canvas.getScale(); // currently we only use Y, same value is used for X
            double oldScale = scale;

            if (event.getDeltaY() < 0) {
                scale /= delta;
            } else {
                scale *= delta;
            }
            scale = clamp(scale, MIN_SCALE, MAX_SCALE);

            double f = (scale / oldScale) - 1;
            double dx = (event.getSceneX() - (canvas.getBoundsInParent().getWidth() / 2 + canvas.getBoundsInParent().getMinX()));
            double dy = (event.getSceneY() - (canvas.getBoundsInParent().getHeight() / 2 + canvas.getBoundsInParent().getMinY()));

            canvas.setScale(scale);
            canvas.setPivot(f * dx, f * dy);
            canvas.setTranslateX(clamp(canvas.getTranslateX(), minX(), maxX()));
            canvas.setTranslateY(clamp(canvas.getTranslateX(), minY(), maxY()));

            event.consume();
        }
    };

    /**
     * Returns the value within the min and max boundaries
     * @param value - value
     * @param min - lower bounds
     * @param max - upper bounds
     * @return - the value within the boundaries
     */
    private static double clamp(double value, double min, double max) {

        if (Double.compare(value, min) < 0)
            return min;

        if (Double.compare(value, max) > 0)
            return max;

        return value;
    }

    /**
     * Returns the minimum value of x to ensure the canvas being within the scene
     * @return minimum value for x
     */
    private double minX() {
        return canvas.getWidth() / 2 - canvas.getWidth() / 2 * canvas.getScale() - (canvas.getWidth() - canvas.getScene().getWidth());
    }

    /**
     * Returns the minimum value of y to ensure the canvas being within the scene
     * @return minimum value for y
     */
    private double minY() {
        return canvas.getHeight() / 2 - canvas.getHeight() / 2 * canvas.getScale() - (canvas.getHeight() - canvas.getScene().getHeight());
    }

    /**
     * Returns the maximum value of x to ensure the canvas being within the scene
     * @return maximum value for x
     */
    private double maxX() {
        return canvas.getWidth() / 2 * canvas.getScale() - canvas.getWidth() / 2;
    }

    /**
     * Returns the maximum value of y to ensure the canvas being within the scene
     * @return maximum value for y
     */
    private double maxY() {
        return canvas.getHeight() / 2 * canvas.getScale() - canvas.getHeight() / 2;
    }
}
