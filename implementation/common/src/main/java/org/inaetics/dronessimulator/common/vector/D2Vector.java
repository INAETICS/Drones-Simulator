package org.inaetics.dronessimulator.common.vector;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Two-dimensional vector implementation.
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public class D2Vector {
    /**
     * The unity vector.
     */
    public static final D2Vector UNIT = new D2Vector(1, 1);

    /**
     * X coordinate of this vector.
     */
    @Getter
    private final double x;

    /**
     * Y coordinate of this vector.
     */
    @Getter
    private final double y;

    /**
     * Instantiates a new vector from the origin.
     */
    public D2Vector() {
        this(0, 0);
    }

    /**
     * Produces a new vector which is the result of adding the given vector to this vector.
     *
     * @param other The vector to add to this vector.
     * @return The resulting vector.
     */
    public D2Vector add(D2Vector other) {
        return new D2Vector(this.getX() + other.getX(), this.getY() + other.getY());
    }

    /**
     * Produces a new vector which is the result of subtracting the given vector from this vector.
     *
     * @param other The vector to subtract from this vector.
     * @return The resulting vector.
     */
    public D2Vector sub(D2Vector other) {
        return new D2Vector(this.getX() - other.getX(), this.getY() - other.getY());
    }

    /**
     * Calculates the length of this vector.
     *
     * @return The length.
     */
    public double length() {
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
    }

    /**
     * Produces a new vector which is the result of scaling (multiplying) this vector with the given scalar.
     *
     * @param scalar The value to multiply this vector with.
     * @return The resulting vector.
     */
    public D2Vector scale(double scalar) {
        return new D2Vector(this.getX() * scalar, this.getY() * scalar);
    }

}
