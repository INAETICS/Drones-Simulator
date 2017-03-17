package org.inaetics.isep;

public class D3Vector {
    public static D3Vector UNIT = new D3Vector(1,1,1);
    private final double x;
    private final double y;
    private final double z;

    public D3Vector() {
        this(0,0,0);
    }

    public D3Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    // Arrow from 0,0,0 to a + b
    public D3Vector add(D3Vector b) {
        return new D3Vector(this.getX() + b.getX(), this.getY() + b.getY(), this.getZ() + b.getZ());
    }

    // Arrow from b to a
    public D3Vector sub(D3Vector b) {
        return new D3Vector(this.getX() - b.getX(), this.getY() - b.getY(), this.getZ() - b.getZ());
    }

    public D3Vector scale(double scalar) {
        return new D3Vector(scalar * this.getX(), scalar * this.getY(), scalar * this.getZ());
    }

    // Return a vector in same direction with length 1
    public D3Vector normalize() {
        return this.scale(1.0d / this.length());
    }

    public double length() {
        return Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2) + Math.pow(this.getZ(), 2));
    }

    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
