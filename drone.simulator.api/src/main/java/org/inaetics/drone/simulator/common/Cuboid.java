package org.inaetics.drone.simulator.common;

public class Cuboid {

    private final D3Vector cornerPoint;
    private final double width;
    private final double height;
    private final double length;

    public Cuboid(D3Vector cornerPoint, double width, double height, double length) {
        this.cornerPoint = cornerPoint;
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public D3Vector getCornerPoint() {
        return cornerPoint;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getLength() {
        return length;
    }
}
