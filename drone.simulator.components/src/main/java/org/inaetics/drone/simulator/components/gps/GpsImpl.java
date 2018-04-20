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

package org.inaetics.drone.simulator.components.gps;

import org.inaetics.drone.simulator.api.gps.Gps;
import org.inaetics.drone.simulator.api.gps.PlatformInfo;
import org.inaetics.drone.simulator.spi.Constants;
import org.inaetics.drone.simulator.spi.costs.ComponentCost;
import org.inaetics.pubsub.api.pubsub.Subscriber;
import org.osgi.service.log.LogService;

import java.util.UUID;

public class GpsImpl implements Gps, ComponentCost, Subscriber {

    private volatile LogService log;

    private final UUID sensorUUid = UUID.randomUUID();

    private PlatformInfo latestPlatformInfo = null;

    public void start() {
        log.log(LogService.LOG_INFO, "Starting Gps Component");
        //TODO start gps impl
    }

    public void stop() {
        log.log(LogService.LOG_INFO, "Stopping Gps Component");
        //TODO stop gps
    }

    @Override
    public void receive(Object o, MultipartCallbacks multipartCallbacks) {
        if(o instanceof PlatformInfo){
            latestPlatformInfo = (PlatformInfo) o;
            //TODO: perhaps update timeBetweenUpdates?
        }
    }

    @Override
    public String getComponentName() {
        return "Gps";
    }

    @Override
    public double getCost() {
        return Constants.DRONE_COMPONENTS_GPS_COST;
    }

    @Override
    public double getTimeBetweenUpdates() {
        //TODO:
        return 0;
    }

    @Override
    public PlatformInfo getLatestPlatformInfo() {
        return latestPlatformInfo;
    }
}
