package org.inaetics.dronessimulator.gameengine.common;

/**
 * The size of the hitbox of an entity in the physics engine.
 * The size oes not rotate and is static. It is relative to the position of the entity.
 * Size specifies a three-dimensional box around an entitiy, the position of the entity is assumed to be in the centre
 * of this box. This box is axis-aligned.
 */
public class Size {
    public Size(double width, double depth, double height) {
        this.width = width;
        this.depth = depth;
        this.height = height;
    }

    /** Width of the entity. */
    private final double width;

    /** Depth of the entity. */
    private final double depth;

    /** Height of the entity. */
    private final double height;

    public double getWidth() {
        return width;
    }

    public double getDepth() {
        return depth;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "Size{" +
                "width=" + width +
                ", depth=" + depth +
                ", height=" + height +
                '}';
    }
}
