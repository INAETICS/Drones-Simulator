/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/

package org.inaetics.drone.simulator.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Three-dimensional polar coordinate.
 */
public class D3PolarCoordinate implements Serializable {
    /** The unity coordinate. */
    public static final D3PolarCoordinate UNIT = new D3PolarCoordinate(0,0, 1);

    /** The angle between the x and y axis. */
    private final double angle1_x_y; // Between 0 and 2pi

    /** The angle between the x and z axis. */
    private final double angle2_x_z; // Between -0.5 * pi and 0.5 * pi

    /** The distance to the coordinate. */
    private final double length;

    /**
     * Instantiates a new three-dimensional unity polar coordinate.
     */
    public D3PolarCoordinate() {
        this(0,0,1);
    }

    /**
     * Instantiates a new three-dimensional polar coordinate with the given coordinates.
     * @param angle1_x_y_ The angle between the x and y axis.
     * @param angle2_x_z_ The angle between the x and z axis.
     * @param length_ The distance to the coordinate.
     */
    public D3PolarCoordinate(double angle1_x_y_, double angle2_x_z_, double length_) {
        // Change angles to keep the length always positive.
        if (length_ < 0) {
            angle1_x_y_ = angle1_x_y_ + Math.PI;
            angle2_x_z_ = -1 * angle2_x_z_;
            length_ = -1 * length_;
        }

        // Normalize the angles.
        Double[] normalizedAngles = normalizeAngles(angle1_x_y_, angle2_x_z_);

        this.angle1_x_y = normalizedAngles[0];
        this.angle2_x_z = normalizedAngles[1];
        this.length = length_;
    }

    /**
     * Normalizes the given angles such that the first angle is between 0 and 2pi and the second angle is between -0.5pi
     * and 0.5pi.
     * @param angle1_x_y The angle between the x and y axis.
     * @param angle2_x_z The angle between the x and z axis.
     * @return A tuple containing the normalized angles in the order they were given.
     */
    private static Double[] normalizeAngles(double angle1_x_y, double angle2_x_z) {
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

        return new Double[] {a1_, a2_};
    }

    /**
     * Returns the angle between the x and y axis in radians.
     * @return The angle between the x and y axis.
     */
    public double getAngle1() {
        return angle1_x_y;
    }

    /**
     * Returns the angle between the x and y axis in degrees.
     * @return The angle between the x and y axis.
     */
    public double getAngle1Degrees() {
        return radianToDegrees(this.angle1_x_y);
    }

    /**
     * Returns the angle between the x and z axis in radians.
     * @return The angle between the x and z axis.
     */
    public double getAngle2() {
        return angle2_x_z;
    }

    /**
     * Returns the angle between the x and z axis in degrees.
     * @return The angle between the x and z axis.
     */
    public double getAngle2Degrees() {
        return radianToDegrees(this.angle2_x_z);
    }

    /**
     * Produces a new coordinate which is rotated with the given angles.
     * @param angle1_x_y The relative rotation between the x and y axis.
     * @param angle2_x_z The relative rotation between the x and z axis.
     * @return The produced coordinate.
     */
    public D3PolarCoordinate rotate(double angle1_x_y, double angle2_x_z) {
        return new D3PolarCoordinate(this.angle1_x_y + angle1_x_y, this.angle2_x_z + angle2_x_z, this.length);
    }

    /**
     * Produces a new coordinate which is moved (in distance) with the given factor.
     * @param scalar The scalar for the distance.
     * @return The produced coordinate.
     */
    public D3PolarCoordinate scale(double scalar) {
        return new D3PolarCoordinate(this.angle1_x_y, this.angle2_x_z, this.length * scalar);
    }

    /**
     * Returns the distance of this coordinate.
     * @return The distance of this coordinate.
     */
    public double getLength() {
        return length;
    }

    /**
     * Converts this polar coordinate to a vector.
     * @return The resulting vector.
     */
    public D3Vector toVector() {
        double xy_length = Math.cos(this.angle2_x_z) * this.length;
        double x_length = Math.cos(this.angle1_x_y) * xy_length;
        double y_length = Math.sin(this.angle1_x_y) * xy_length;
        double z_length = Math.sin(this.angle2_x_z) * this.length;

        return new D3Vector(x_length, y_length, z_length);
    }

    /**
     * Returns the string representation of this coordinate.
     * @return The string representation.
     */
    public String toString() {
        return "(a1:" + this.angle1_x_y + ", a2: " + this.angle2_x_z + " l:" + this.length + ")";
    }

    public static D3PolarCoordinate fromString(String str){
        Pattern pattern = Pattern.compile("\\(a1:(-?[0-9.]*), a2:(-?[0-9.]*), l:(-?[0-9.]*)\\)");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            return new D3PolarCoordinate(Double.parseDouble(matcher.group(1)), Double.parseDouble(matcher.group(2)), Double.parseDouble(matcher.group(3)));
        }

        return null;
    }

    /**
     * Tests whether the given object is equal to this coordinate.
     * @param o The object to test.
     * @return Whether the given object is equal to this coordinate.
     */
    @Override
    public boolean equals(Object o) {
        if(o instanceof D3PolarCoordinate) {
            D3PolarCoordinate other = (D3PolarCoordinate) o;
            return BigDecimal.valueOf(this.getAngle1()).compareTo(BigDecimal.valueOf(other.getAngle1())) == 0
                && BigDecimal.valueOf(this.getAngle2()).compareTo(BigDecimal.valueOf(other.getAngle2())) == 0
                && BigDecimal.valueOf(this.getLength()).compareTo(BigDecimal.valueOf(other.getLength())) == 0;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (int) Math.round(this.getAngle1() + this.getAngle2() + this.getLength());
    }

    /**
     * Converts the given angle in radians to degrees.
     * @param radians The angle in radians.
     * @return The given angle in degrees.
     */
    public static double radianToDegrees(double radians) {
        return (radians / Math.PI) * 180;
    }

    /**
     * Converts the given angle in degrees to radians.
     * @param degrees The angle in degrees.
     * @return The given angle in radians.
     */
    public static double degreesToRadian(double degrees) {
        return (degrees / 180) * Math.PI;
    }
}
