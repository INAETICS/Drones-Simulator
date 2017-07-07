package org.inaetics.dronessimulator.visualisation;

/**
 * Class providing the settings for the visualisation
 */
class Settings {

    /** Width of the window */
    final static double SCENE_WIDTH = org.inaetics.dronessimulator.common.Settings.ARENA_WIDTH;
    /** Height of the window */
    final static double SCENE_HEIGHT = org.inaetics.dronessimulator.common.Settings.ARENA_DEPTH;

    /** Width of the canvas */
    final static double CANVAS_WIDTH = org.inaetics.dronessimulator.common.Settings.ARENA_WIDTH;
    /** Height of the canvas */
    final static double CANVAS_HEIGHT = org.inaetics.dronessimulator.common.Settings.ARENA_DEPTH;

    /** Width of the fully zoomed drone */
    final static int DRONE_WIDTH = 128;
    /** Height of the fully zoomed drone */
    final static int DRONE_HEIGHT = 128;

    /** Height of the fully zoomed bullet */
    final static int BULLET_HEIGHT = 50;

    /** Number of image columns in the drone sprite image */
    final static int DRONE_SPRITE_COLUMNS = 4;
    /** Width of each image in the drone sprite image */
    final static int SPRITE_WIDTH = 256;
    /** Height of each image in the drone sprite image */
    final static int SPRITE_HEIGTH = 256;
}
