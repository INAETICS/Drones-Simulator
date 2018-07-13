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

package org.inaetics.drone.simulator.spi;

public class Constants {
    //from engine to drones: DroneState, GameState. ?KillEvent?
    public static final String STATE_UPDATE_TOPIC_NAME = "state-update";

    //from drones to engine: BulletEvent, DroneAnnouncement, ?EngineEvent?, etc
    public static final String DRONE_UPDATE_TOPIC_NAME = "drone-update";

    //COSTS
    public static final double DRONE_COMPONENTS_GUN_COST = 40.0;
    public static final double DRONE_COMPONENTS_RADAR_COST = 100.0;
    //TODO etc
}
