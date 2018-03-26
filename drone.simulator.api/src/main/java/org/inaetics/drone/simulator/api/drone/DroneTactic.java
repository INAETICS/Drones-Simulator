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

package org.inaetics.drone.simulator.api.drone;

import java.util.UUID;

public interface DroneTactic {
    public UUID getDroneId();
    
    public void reset();
    public void pause();
    public void cont(); //continue

    /**
     * Provided time is the time in seconds since the start of the 'mission'
     * Note that the simulation can be (de)accelerated, resulting in a time diff compared to the actual time (System.getTime())
     * The drone step will be called with a frequency of TBD (10?)
     */
    public void step(double time);
}
