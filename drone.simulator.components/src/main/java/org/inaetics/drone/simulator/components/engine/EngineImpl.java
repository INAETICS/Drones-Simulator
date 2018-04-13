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

package org.inaetics.drone.simulator.components.engine;

import org.inaetics.drone.simulator.api.engine.Engine;
import org.inaetics.drone.simulator.common.D3Vector;
import org.inaetics.drone.simulator.spi.Constants;
import org.inaetics.drone.simulator.spi.costs.ComponentCost;
import org.osgi.service.log.LogService;

import java.util.List;

public class EngineImpl implements Engine, ComponentCost {

    @Override
    public String getComponentName() {
        return "Engine";
    }

    @Override
    public double getCost() {
        return Constants.DRONE_COMPONENTS_ENGINE_COST;
    }

    private volatile LogService log;

    @Override
    public String getName() {
        return "simple-Engine";
    }

    @Override
    public double getMaxSpeed() {
        return 0;
    }

    @Override
    public void setDesiredSpeed(double desiredSpeed) {

    }

    @Override
    public double getDesiredSpeed() {
        return 0;
    }

    @Override
    public double getCurrentSpeed() {
        return 0;
    }

    @Override
    public void setDestination(D3Vector destination) {

    }

    @Override
    public void setDestinations(List<D3Vector> destinations) {

    }

    @Override
    public void clearDestinations() {

    }

    @Override
    public List<D3Vector> getDestinations() {
        return null;
    }

    public void start() {
        log.log(LogService.LOG_INFO, "Starting Engine Component");
        //TODO start Engine
    }

    public void stop() {
        log.log(LogService.LOG_INFO, "Stopping Engine Component");
        //TODO stop Engine
    }
}
