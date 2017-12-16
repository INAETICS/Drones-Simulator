package org.inaetics.dronessimulator.visualisation.controls;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * Listeners for making the nodes draggable via left mouse button. Considers if parent is zoomed.
 */
public class NodeGestures {

    /** mouse drag context */
    private final DragContext nodeDragContext = new DragContext();
    /** Canvas that can be zoomed and panned */
    private final PannableCanvas canvas;

    /**
     * Instantiates the listeners for making the canvas pannable and zoomable
     * @param canvas - canvas
     */
    public NodeGestures(PannableCanvas canvas) {
        this.canvas = canvas;
    }

    /**
     * Mouse event handler for clicking with the right mouse button
     *
     * This sets the x and y coordinates, which are used when dragging
     */
    private EventHandler<MouseEvent> onMousePressedEventHandler = event -> {

        // left mouse button => dragging
        if (!event.isPrimaryButtonDown())
            return;

        nodeDragContext.mouseAnchorX = event.getSceneX();
        nodeDragContext.mouseAnchorY = event.getSceneY();

        Node node = (Node) event.getSource();
        System.out.format("%10f,%10f%n", node.getTranslateX(), node.getTranslateY());

        nodeDragContext.translateAnchorX = node.getTranslateX();
        nodeDragContext.translateAnchorY = node.getTranslateY();

    };

    /**
     * Mouse event handler for dragging with the right mouse button
     *
     * When dragging the scene follows the mouse cursor
     * The coordinates are corrected to the scale on which is zoomed to
     */
    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {

            // left mouse button => dragging
            if (!event.isPrimaryButtonDown())
                return;

            double scale = canvas.getScale();

            Node node = (Node) event.getSource();

            node.setTranslateX(nodeDragContext.translateAnchorX + ((event.getSceneX() - nodeDragContext.mouseAnchorX) / scale));
            node.setTranslateY(nodeDragContext.translateAnchorY + ((event.getSceneY() - nodeDragContext.mouseAnchorY) / scale));

            event.consume();

        }
    };
}
