package org.inaetics.dronessimulator.visualisation.controls;

/**
 * Mouse drag context used for scene and nodes.
 */
class DragContext {

    /** X position of the mouse */
    double mouseAnchorX;
    /** Y position of the mouse */
    double mouseAnchorY;

    /** X position relative to a canvas (also with zoom and pan) */
    double translateAnchorX;
    /** Y position relative to a canvas (also with zoom and pan) */
    double translateAnchorY;
}
