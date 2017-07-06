package org.inaetics.dronessimulator.common;

/**
 * Two-dimensional vector implementation.
 */
public class D2Vector {
    /** The unity vector. */
    public transient final static D2Vector UNIT = new D2Vector(1,1);

    /** X coordinate of this vector. */
    private final double x;

    /** Y coordinate of this vector. */
    private final double y;

    /**
     * Instantiates a new vector from the origin.
     */
    public D2Vector() {
        this(0,0);
    }

    /**
     * Instantiates a new vector with the given coordinates.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    public D2Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x coordinate of this vector.
     * @return The x coordinate.
     */
    public double getX() {
        return this.x;
    }

    /**
     * Returns the y coordinate of this vector.
     * @return The y coordinate.
     */
    public double getY() {
        return this.y;
    }

    /**
     * Produces a new vector which is the result of adding the given vector to this vector.
     * @param other The vector to add to this vector.
     * @return The resulting vector.
     */
    public D2Vector add(D2Vector other) {
        return new D2Vector(this.getX() + other.getX(), this.getY() + other.getY());
    }

    /**
     * Produces a new vector which is the result of subtracting the given vector from this vector.
     * @param other The vector to subtract from this vector.
     * @return The resulting vector.
     */
    public D2Vector sub(D2Vector other) {
        return new D2Vector(this.getX() - other.getX(), this.getY() - other.getY());
    }

    /**
     * Calculates the length of this vector.
     * @return The length.
     */
    public double length() {
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
    }

    /**
     * Produces a new vector which is the result of scaling (multiplying) this vector with the given scalar.
     * @param scalar The value to multiply this vector with.
     * @return The resulting vector.
     */
    public D2Vector scale(double scalar) {
        return new D2Vector(this.getX() * scalar, this.getY() * scalar);
    }

    /**
     * Calculates the inproduct of this vector and the given vector.
     * @param other The other vector.
     * @return The inproduct of this vector and the other vector.
     */
    public double in_product(D3Vector other) {
        return this.getX() * other.getX() + this.getY() * other.getY();
    }
}
