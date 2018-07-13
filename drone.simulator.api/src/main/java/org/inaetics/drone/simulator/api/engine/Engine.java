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

package org.inaetics.drone.simulator.api.engine;

import org.inaetics.drone.simulator.common.D3Vector;

import java.util.List;

public interface Engine {

    public String getName();

    public double getMaxSpeed();

    public void setDesiredSpeed(double desiredSpeed);
    public double getDesiredSpeed();
    public double getCurrentSpeed();

    public void setDestination(D3Vector destination);
    public void setDestinations(List<D3Vector> destinations);
    public void clearDestinations();

    /**
     * Returns a copy of the destinations
     */
    public List<D3Vector> getDestinations();
}
