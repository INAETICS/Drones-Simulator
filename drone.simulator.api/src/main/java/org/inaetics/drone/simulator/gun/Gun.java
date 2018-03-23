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

package org.inaetics.drone.simulator.gun;

import org.inaetics.drone.simulator.common.D3Vector;

import java.util.List;

public interface Gun {

    String getName();

    double getMaxDistance();
    double getMuzzleVelocity();
    double getTimeBetweenShots();
    double getShotsLeft();

    void aimAndFireAt(D3Vector pos);

    /**
     * Returns the estimate time needed to aim at provided location
     */
    double getTimeToAimAt(D3Vector pos);
}
