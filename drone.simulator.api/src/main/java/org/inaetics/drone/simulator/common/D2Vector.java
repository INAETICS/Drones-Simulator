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

    /**
     * Y coordinate of this vector.
     */
    private final double y;

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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
