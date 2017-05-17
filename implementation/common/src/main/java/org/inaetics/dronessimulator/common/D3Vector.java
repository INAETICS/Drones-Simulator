package org.inaetics.dronessimulator.common;

import java.io.Serializable;

public class D3Vector implements Serializable {
    public transient static D3Vector UNIT = new D3Vector(1,1,1);
    private final double x;
    private final double y;
    private final double z;

    private transient Double length = null;

    public D3Vector() {
        this(0,0,0);
    }

    /*
     * @requires -0.5 * Math.PI <= z <= 0.5 * Math.PI
     */
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
        if(length == null) {
            this.length = Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2) + Math.pow(this.getZ(), 2));
        }

        return length;
    }

    public double in_product(D3Vector other) {
        return this.getX() * other.getX() + this.getY() * other.getY() + this.getZ() * other.getZ();
    }

    public D3PoolCoordinate toPoolCoordinate() {
        double angle1_x_y;

        // atan relation only available if x != 0!
        if(this.x > 0 && this.y > 0) {
            angle1_x_y = Math.atan(this.y / this.x);
        } else if(this.x > 0 && this.y < 0) {
            angle1_x_y = 2 * Math.PI - Math.atan(Math.abs(this.y) / this.x);
        } else if(this.x < 0 && this.y > 0) {
            angle1_x_y = Math.PI - Math.atan(this.y / Math.abs(this.x));
        } else if  (this.x < 0 && this.y < 0) {
            angle1_x_y = Math.PI + Math.atan(this.y / this.x);
        } else if(this.x == 0 && this.y != 0) {
            // x == 0, so look where y is
            if(this.y > 0) {
                angle1_x_y = 0.5 * Math.PI;
            } else if(this.y < 0) {
                angle1_x_y = 1.5 * Math.PI;
            } else {
                angle1_x_y = 0;
            }
        } else {
            // y == 0, so look where x is
            if(this.x > 0) {
                angle1_x_y = 0;
            } else if(this.x < 0) {
                angle1_x_y = Math.PI;
            } else {
                angle1_x_y = 0;
            }
        }

        double angle2_x_z;

        // atan relation only available if x != 0 || y != 0
        if(this.x != 0 || this.y != 0) {
            angle2_x_z = Math.atan(this.z / Math.sqrt(Math.pow(this.x,2) + Math.pow(this.y,2)));
        } else {
            // x + y == 0, look where z is pointing
            if(z > 0) {
                angle2_x_z = 0.5 * Math.PI;
            } else if(z < 0) {
                angle2_x_z = -0.5 * Math.PI;
            } else {
                angle2_x_z = 0;
            }
        }

        return new D3PoolCoordinate(angle1_x_y, angle2_x_z, this.length());
    }

    // Angle is defined as the angle from this to angle_between
    public double angle_between(D3Vector other) {
        double in_product = this.in_product(other);
        double length_product = this.length() * other.length();

        return Math.acos(in_product / length_product);
    }

    public String toString() {
        return "(x:" + x + ", y:" + y + ", z:" + z + ")";
    }

    public boolean equals(D3Vector other) {
        return this.getX() == other.getX() && this.getY() == other.getY() && this.getZ() == other.getZ();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof D3Vector && this.equals((D3Vector) other);
    }
}
