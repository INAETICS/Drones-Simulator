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
