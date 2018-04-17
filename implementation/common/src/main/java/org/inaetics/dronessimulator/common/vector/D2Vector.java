package org.inaetics.dronessimulator.common.vector;

import java.util.Objects;

/**
 * Two-dimensional vector implementation.
 */
public class D2Vector {
    public D2Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }
    /**
     * The unity vector.
     */
    public static final D2Vector UNIT = new D2Vector(1, 1);

    /**
     * X coordinate of this vector.
     */
    private final double x;

    public double getX() {
        return x;
    }

    /**
     * Y coordinate of this vector.
     */
    private final double y;

    public double getY() {
        return y;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof D2Vector)) return false;
        D2Vector d2Vector = (D2Vector) o;
        return Double.compare(d2Vector.x, x) == 0 &&
                Double.compare(d2Vector.y, y) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(x, y);
    }
}
