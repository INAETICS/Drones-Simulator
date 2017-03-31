package org.inaetics.isep;


public class D2Vector {
    public static D2Vector UNIT = new D2Vector(1,1);
    private final double x;
    private final double y;

    private Double length = null;

    public D2Vector() {
        this(0,0);
    }

    public D2Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public D2Vector add(D2Vector other) {
        return new D2Vector(this.getX() + other.getX(), this.getY() + other.getY());
    }

    public D2Vector sub(D2Vector other) {
        return new D2Vector(this.getX() - other.getX(), this.getY() - other.getY());
    }

    public double length() {
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
    }

    public double in_product(D3Vector other) {
        return this.getX() * other.getX() + this.getY() * other.getY();
    }
}
