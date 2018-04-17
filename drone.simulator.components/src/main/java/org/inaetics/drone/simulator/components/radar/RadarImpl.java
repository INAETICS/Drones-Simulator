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

package org.inaetics.drone.simulator.components.radar;

import org.inaetics.drone.simulator.api.radar.Detection;
import org.inaetics.drone.simulator.api.radar.DetectionListener;
import org.inaetics.drone.simulator.api.radar.Radar;
import org.inaetics.drone.simulator.spi.Constants;
import org.inaetics.drone.simulator.spi.costs.ComponentCost;
import org.inaetics.drone.simulator.spi.events.DroneState;
import org.inaetics.drone.simulator.spi.events.StateEvent;
import org.osgi.service.log.LogService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class RadarImpl implements Radar, ComponentCost {
//TODO enable line when the IANETICS pubsub dep is added
//public class RadarImpl implements Radar, Subscriber {

    private volatile LogService log;

    private final UUID sensorUUid = UUID.randomUUID();
    private final List<DetectionListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public UUID getSensorId() {
        return sensorUUid;
    }

    @Override
    public String getName() {
        return "simple radar";
    }

    @Override
    public double getRotationFrequency() {
        return 0.25;
    }

    public void addListener(DetectionListener l) {
        listeners.add(l);
    }

    public void removeListener(DetectionListener l) {
        listeners.remove(l);
    }

    public void start() {
        log.log(LogService.LOG_INFO, "Starting Radar Component");
        //TODO start radar impl
    }

    public void stop() {
        log.log(LogService.LOG_INFO, "Stopping Radar Component");
        //TODO stop radar
    }

    /** TODO enable method when the INATICS PubSub dep is added
    @Override
    public void receive(Object o, MultipartCallbacks multipartCallbacks) {
        if (o instanceof StateEvent) {
            processDroneUpdate((StateEvent)o);
        }
    }*/

    protected void processDroneUpdate(StateEvent stateEvent) {
        //TODO update detected cache based on drone state and
        //update listener based on the update speed and current area
        //under consideration
        List<DroneState> droneStates = stateEvent.getStates();
        ArrayList<Detection> detections = new ArrayList<Detection>(droneStates.size());
        for (int i = 0; i < droneStates.size(); i++) {
            DroneState droneState = droneStates.get(i);
            int timeValidity = 100000; //TODO
            Detection detection = new Detection(UUID.randomUUID(),
                    timeValidity,
                    droneState.getPosition(),
                    droneState.getVelocity());
        }

        for (DetectionListener listener : listeners){
            listener.processDetections(sensorUUid, detections);
        }
    }

    @Override
    public String getComponentName() {
        return "Radar";
    }

    @Override
    public double getCost() {
        return Constants.DRONE_COMPONENTS_RADAR_COST;
    }
}
