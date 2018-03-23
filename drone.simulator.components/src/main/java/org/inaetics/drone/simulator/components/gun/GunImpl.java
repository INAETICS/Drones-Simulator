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

package org.inaetics.drone.simulator.components.gun;

import org.inaetics.drone.simulator.api.gun.Gun;
import org.inaetics.drone.simulator.common.D3Vector;

public class GunImpl implements Gun {

    @Override
    public String getName() {
        return "simple-gun";
    }

    @Override
    public double getMaxDistance() {
        return 0; //TODO
    }

    @Override
    public double getMuzzleVelocity() {
        return 0; //TODO
    }

    @Override
    public double getTimeBetweenShots() {
        return 0; //TODO
    }

    @Override
    public double getShotsLeft() {
        return 0; //TODO
    }

    @Override
    public void aimAndFireAt(D3Vector pos) {
        //TODO
    }

    @Override
    public double getTimeToAimAt(D3Vector pos) {
        return 0; //TODO
    }

    public void start() {
        //TODO start gun
    }

    public void stop() {
        //TODO stop gun
    }
}
