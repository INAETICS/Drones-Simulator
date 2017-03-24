package org.inaetics.isep;

public class D3PoolCoordinate {
    public static final D3PoolCoordinate UNIT = new D3PoolCoordinate(0,0, 1);
    public final double angle1_x_y;
    public final double angle2_x_z;
    public final double length;

    public D3PoolCoordinate(double angle1_x_y, double angle2_x_z, double length) {
        this.angle1_x_y = angle1_x_y;
        this.angle2_x_z = angle2_x_z;
        this.length = length;
    }

    public double getAngle1() {
        return angle1_x_y;
    }

    public double getAngle2() {
        return angle2_x_z;
    }

    public double getLength() {
        return length;
    }

    public D3Vector toVector() {
        double xy_length = Math.cos(this.angle2_x_z) * this.length;
        double x_length = Math.cos(this.angle1_x_y) * xy_length;
        double y_length = Math.sin(this.angle1_x_y) * xy_length;
        double z_length = Math.sin(this.angle2_x_z) * this.length;

        return new D3Vector(x_length, y_length, z_length);
    }

    public String toString() {
        return "(a1:" + this.angle1_x_y + ", a2: " + this.angle2_x_z + " l:" + this.length + ")";
    }

    public boolean equals(D3PoolCoordinate other) {
        return this.getAngle1() == other.getAngle1() && this.getAngle2() == other.getAngle2() && this.getLength() == other.getLength();
    }

}
