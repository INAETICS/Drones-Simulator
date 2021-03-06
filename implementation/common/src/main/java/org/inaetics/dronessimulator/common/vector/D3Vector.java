package org.inaetics.dronessimulator.common.vector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Three-dimensional vector implementation.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class D3Vector implements Serializable {
    /** The unity vector. */
    public static final transient D3Vector UNIT = new D3Vector(1, 1, 1);
    public static final transient D3Vector ZERO = new D3Vector(0, 0, 0);

    /** X coordinate of this vector. */
    private final double x;

    /** Y coordinate of this vector. */
    private final double y;

    /** Z coordinate of this vector. */
    private final double z;

    /** Length of this vector. */
    private transient Double length = null;

    /**
     * Instantiates a new vector on the origin.
     */
    public D3Vector() {
        this(0,0,0);
    }

    /**
     * Instantiates a new vector with the given coordinates.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param z The z coordinate. Must be between (inclusive) -0.5pi and 0.5pi.
     */
    public D3Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
     * Returns the z coordinate of this vector.
     * @return The z coordinate.
     */
    public double getZ() {
        return this.z;
    }

    /**
     * Produces a new vector which is the result of adding the given vector to this vector.
     * @param b The vector to add to this vector.
     * @return The resulting vector.
     */
    public D3Vector add(D3Vector b) {
        // Arrow from 0,0,0 to a + b
        return new D3Vector(this.getX() + b.getX(), this.getY() + b.getY(), this.getZ() + b.getZ());
    }

    /**
     * Produces a new vector which is the result of subtracting the given vector from this vector.
     * @param b The vector to subtract from this vector.
     * @return The resulting vector.
     */
    public D3Vector sub(D3Vector b) {
        // Arrow from b to a
        return new D3Vector(this.getX() - b.getX(), this.getY() - b.getY(), this.getZ() - b.getZ());
    }

    /**
     * Produces a new vector which is the result of scaling (multiplying) this vector with the given scalar.
     * @param scalar The value to multiply this vector with.
     * @return The resulting vector.
     */
    public D3Vector scale(double scalar) {
        return new D3Vector(scalar * this.getX(), scalar * this.getY(), scalar * this.getZ());
    }

    /**
     * Produces a new vector in the same direction as this vector with a length of 1.
     * @return The resulting vector.
     */
    public D3Vector normalize() {
        return this.scale(1.0d / this.length());
    }

    /**
     * Calculates the length of this vector.
     * @return The length.
     */
    public double length() {
        if(length == null) {
            this.length = Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2) + Math.pow(this.getZ(), 2));
        }

        return length;
    }

    /**
     * Calculates the inproduct of this vector and the given vector.
     * @param other The other vector.
     * @return The inproduct of this vector and the other vector.
     */
    public double in_product(D3Vector other) {
        return this.getX() * other.getX() + this.getY() * other.getY() + this.getZ() * other.getZ();
    }

    /**
     * Calculates the distance between this vector and the given vector.
     * @param other The other vector.
     * @return The distance between this vector and the other vector.
     */
    public double distance_between(D3Vector other) {
        return this.sub(other).length();
    }

    /**
     * Converts this vector to a three-dimensional polar coordinate.
     * @return The polar coordinate.
     */
    public D3PolarCoordinate toPoolCoordinate() {
        double angle1_x_y;
        BigDecimal bigX = BigDecimal.valueOf(this.x);
        BigDecimal bigY = BigDecimal.valueOf(this.y);

        // atan relation only available if x != 0!
        if(this.x > 0 && this.y > 0) {
            angle1_x_y = Math.atan(this.y / this.x);
        } else if(this.x > 0 && this.y < 0) {
            angle1_x_y = 2 * Math.PI - Math.atan(Math.abs(this.y) / this.x);
        } else if(this.x < 0 && this.y > 0) {
            angle1_x_y = Math.PI - Math.atan(this.y / Math.abs(this.x));
        } else if  (this.x < 0 && this.y < 0) {
            angle1_x_y = Math.PI + Math.atan(this.y / this.x);
        } else if(  bigX.compareTo(BigDecimal.ZERO) == 0
                && bigY.compareTo(BigDecimal.ZERO) != 0
                 ) {
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
            if(this.x < 0) {
                angle1_x_y = Math.PI;
            } else {
                angle1_x_y = 0;
            }
        }

        double angle2_x_z;

        // atan relation only available if x != 0 || y != 0
        if( bigX.compareTo(BigDecimal.ZERO) != 0
         || bigY.compareTo(BigDecimal.ZERO) != 0
          ) {
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

        return new D3PolarCoordinate(angle1_x_y, angle2_x_z, this.length());
    }

    /**
     * Caculates the angle from this vector to the given vector.
     * @param other The other vector.
     * @return The angle between this vector to the given vector.
     */
    public double angle_between(D3Vector other) {
        double in_product = this.in_product(other);
        double length_product = this.length() * other.length();

        return Math.acos(in_product / length_product);
    }

    /**
     * Returns the string representation of this vector.
     * @return The string representation of this vector.
     */
    public String toString() {
        return "(x:" + x + ", y:" + y + ", z:" + z + ")";
    }

    public String toString(int length) {
        String formatString = "(x:%." + length + "f, y:%." + length + "f, z:%." + length + "f)";
        return String.format(formatString, x, y, z);
    }

    public static D3Vector fromString(String str) {
        Pattern pattern = Pattern.compile("\\(x:(-?[0-9.]*), y:(-?[0-9.]*), z:(-?[0-9.]*)\\)");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            return new D3Vector(Double.parseDouble(matcher.group(1)), Double.parseDouble(matcher.group(2)), Double.parseDouble(matcher.group(3)));
        }

        return null;
    }

    /**
     * Tests whether the given object is equal to this vector.
     * @param o The object to test.
     * @return Whether the given object is equal to this vector.
     */
    @Override
    public boolean equals(Object o) {
        if(o instanceof D3Vector) {
            D3Vector other = (D3Vector) o;
            return BigDecimal.valueOf(this.getX()).compareTo(BigDecimal.valueOf(other.getX())) == 0
                && BigDecimal.valueOf(this.getY()).compareTo(BigDecimal.valueOf(other.getY())) == 0
                && BigDecimal.valueOf(this.getZ()).compareTo(BigDecimal.valueOf(other.getZ())) == 0;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (int) Math.round(this.getX() + this.getY() + this.getZ());
    }
}
