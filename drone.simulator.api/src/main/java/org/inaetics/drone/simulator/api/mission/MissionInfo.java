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

package org.inaetics.drone.simulator.api.mission;

import org.inaetics.drone.simulator.common.Cuboid;

public interface MissionInfo {

    /**
     * Returns the acceleration factor for the game time.
     * This can be used in simulation to slow down or speed up.
     * 1.0 means 'normal' time
     */
    public double getTimeAcceleration();

    //TODO should this be here or in gps/platform-service?
    public double getCurrentTime();

    public Cuboid getMissionArea();

    public MissionGoal getMissionGoal();

    //TODO mission target assets?
}
