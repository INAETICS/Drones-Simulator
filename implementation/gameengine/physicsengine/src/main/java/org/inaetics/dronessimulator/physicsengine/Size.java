package org.inaetics.dronessimulator.physicsengine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
/**
 * The size of the hitbox of an entity in the physics engine.
 * Does not rotate and is static. Is relative to the position of the entity.
 * Position of entity is assumed to be the centre. Size specifies a box around this center
 * in all directions along the x,y and z axis. This box is axis-aligned.
 */
public class Size {
    /**
     * Width of the entity
     */
    private final double width;
    /**
     * Depth of the entity
     */
    private final double depth;
    /**
     * Height of the entity
     */
    private final double height;
}
