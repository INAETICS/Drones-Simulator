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

package org.inaetics.drone.simulator.api.gps;

import org.inaetics.drone.simulator.common.D3Vector;

public class PlatformInfo {

    private final double timeValidity;
    private final D3Vector position;
    private final D3Vector velocity;
    private final D3Vector acceleration;

    public PlatformInfo(double timeValidity, D3Vector position, D3Vector velocity, D3Vector acceleration) {
        this.timeValidity = timeValidity;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    public double getTimeValidity() {
        return timeValidity;
    }

    public D3Vector getPosition() {
        return position;
    }

    public D3Vector getVelocity() {
        return velocity;
    }

    public D3Vector getAcceleration() {
        return acceleration;
    }
}
