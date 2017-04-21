package org.inaetics.dronesimulator.common;

public class D3PoolCoordinate {
    public static final D3PoolCoordinate UNIT = new D3PoolCoordinate(0,0, 1);
    private final double angle1_x_y; // Between 0 and 2pi
    private final double angle2_x_z; // Between -0.5 * pi and 0.5 * pi
    private final double length;

    public D3PoolCoordinate(double angle1_x_y, double angle2_x_z, double length) {

        if(length < 0) {
            angle1_x_y = angle1_x_y + Math.PI;
            angle2_x_z = -1 * angle2_x_z;
            length = -1 * length;
        }

        Tuple<Double, Double> normalizedAngles = normalizeAngles(angle1_x_y, angle2_x_z);

        this.angle1_x_y = normalizedAngles.getLeft();
        this.angle2_x_z = normalizedAngles.getRight();
        this.length = length;
    }

    private static Tuple<Double, Double> normalizeAngles(double angle1_x_y, double angle2_x_z) {
        double a1_ = angle1_x_y;
        double a2_ = angle2_x_z % (2 * Math.PI);

        if(angle2_x_z > 0.5 * Math.PI) {
            if(angle2_x_z <= Math.PI) {
                // 0.5 PI < angle2 <= PI
                // Mirror a1_
                // a2_ starts at the other side now
                a2_ = Math.PI - a2_;
                a1_ = a1_ + Math.PI;
            } else if(angle2_x_z <= 1.5 * Math.PI) {
                // PI < angle2 <= 1.5 PI
                //Mirror a1_
                // a2_ starts at the other side now

                a2_ = -1 * (a2_ - Math.PI);
                a1_ = a1_ + Math.PI;
            } else {
                // 1.5 PI < angle2 < 2 PI
                a2_ = a2_ - 2 * Math.PI;
            }
        } else if(angle2_x_z < -0.5 * Math.PI) {
            if(angle2_x_z >= -1 * Math.PI) {
                // -0.5 PI < angle2 <= -PI
                // Mirror a1_
                // a2_ starts at the other side now

                a2_ = -1 * Math.PI - a2_;
                a1_ = a1_ + Math.PI;
            } else if(angle2_x_z >= -1.5 * Math.PI) {
                // -PI < angle2 <= -1.5 PI
                //Mirror a1_
                // a2_ starts at the other side now

                a2_ = Math.abs(a2_) - Math.PI;
                a1_ = a1_ + Math.PI;
            } else {
                // -1.5 PI < angle2 <-2 PI
                a2_ = 2 * Math.PI + a2_;
            }
        }

        if(a1_ < 0) {
            a1_ = 2 * Math.PI + (a1_ % (2 * Math.PI));
        } else {
            a1_ = a1_ % (2 * Math.PI);
        }

        return new Tuple<>(a1_, a2_);
    }

    public double getAngle1() {
        return angle1_x_y;
    }

    public double getAngle1Degrees() {
        return radianToDegrees(this.angle1_x_y);
    }

    public double getAngle2() {
        return angle2_x_z;
    }

    public double getAngle2Degrees() {
        return radianToDegrees(this.angle2_x_z);
    }

    public D3PoolCoordinate rotate(double angle1_x_y, double angle2_x_z) {
        return new D3PoolCoordinate(this.angle1_x_y + angle1_x_y, this.angle2_x_z + angle2_x_z, this.length);
    }

    public D3PoolCoordinate scale(double scalar) {
        return new D3PoolCoordinate(this.angle1_x_y, this.angle2_x_z, this.length * scalar);
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

    public static double radianToDegrees(double radians) {
        return (radians / Math.PI) * 180;
    }

    public static double degreesToRadian(double degrees) {
        return (degrees / 180) * Math.PI;
    }
}
