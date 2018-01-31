package org.inaetics.dronessimulator.gameengine.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * The size of the hitbox of an entity in the physics engine.
 * The size oes not rotate and is static. It is relative to the position of the entity.
 * Size specifies a three-dimensional box around an entitiy, the position of the entity is assumed to be in the centre
 * of this box. This box is axis-aligned.
 */
@AllArgsConstructor
@Getter
@ToString
public class Size {
    /** Width of the entity. */
    private final double width;

    /** Depth of the entity. */
    private final double depth;

    /** Height of the entity. */
    private final double height;
}
