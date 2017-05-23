package org.inaetics.dronessimulator.visualisation.controls;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * Listeners for making the scene's canvas draggable and zoomable
 */
public class SceneGestures {

    private static final double MAX_SCALE = 15.0d;
    private static final double MIN_SCALE = 1.0d;

    private DragContext sceneDragContext = new DragContext();

    private PannableCanvas canvas;

    public SceneGestures(PannableCanvas canvas) {
        this.canvas = canvas;
    }

    public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

    public EventHandler<ScrollEvent> getOnScrollEventHandler() {
        return onScrollEventHandler;
    }

    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

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

    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
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
    private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

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

    public static double clamp(double value, double min, double max) {

        if (Double.compare(value, min) < 0)
            return min;

        if (Double.compare(value, max) > 0)
            return max;

        return value;
    }

    private double minX() {
        return canvas.getWidth() / 2 - canvas.getWidth() / 2 * canvas.getScale() - (canvas.getWidth() - canvas.getScene().getWidth());
    }

    private double minY() {
        return canvas.getHeight() / 2 - canvas.getHeight() / 2 * canvas.getScale() - (canvas.getHeight() - canvas.getScene().getHeight());
    }

    private double maxX() {
        return canvas.getWidth() / 2 * canvas.getScale() - canvas.getWidth() / 2;
    }

    private double maxY() {
        return canvas.getHeight() / 2 * canvas.getScale() - canvas.getHeight() / 2;
    }
}
