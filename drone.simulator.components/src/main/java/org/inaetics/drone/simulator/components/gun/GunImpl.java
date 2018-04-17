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

import org.inaetics.drone.simulator.api.gps.Gps;
import org.inaetics.drone.simulator.api.gun.Gun;
import org.inaetics.drone.simulator.common.D3Vector;
import org.inaetics.drone.simulator.spi.Constants;
import org.inaetics.drone.simulator.spi.costs.ComponentCost;
import org.inaetics.drone.simulator.spi.events.BulletEvent;
import org.osgi.service.log.LogService;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

public class GunImpl implements Gun, ComponentCost {

    /**
     * The speed of the bullet
     */
    protected static final double BULLET_SPEED = 150.0;
    /**
     * The maximum distance the gun can aim
     */
    protected static final double MAX_DISTANCE = 1024;
    /**
     * Lowest time between shots
     */
    private static final long BASE_SHOT_TIME_BETWEEN = 500;
    /**
     * Maximum time added to {@link #BASE_SHOT_TIME_BETWEEN}
     */
    private static final int MAX_OFFSET_SHOT_TIME = 1000;

    private static final int BASE_AIM_TIME = 500;


    /**
     * Last time the gun has fired
     */
    private long lastShotAtMs = System.currentTimeMillis();
    /**
     * Next time the gun may fire
     */
    private long nextShotAtMs = lastShotAtMs;

    /**
     * How many shots can still be fired
     */
    private int shotsLeft = 100;


    @Override
    public String getComponentName() {
        return "Gun";
    }

    @Override
    public double getCost() {
        return Constants.DRONE_COMPONENTS_GUN_COST;
    }

    public enum MountLocation {
        TOP,
        BOTTOM
    }

    private final MountLocation gunMountLocation;
    private volatile LogService log;
    private volatile Gps gps;

    public GunImpl(MountLocation loc) {
        this.gunMountLocation = loc;
    }

    @Override
    public String getName() {
        return "simple-gun";
    }

    @Override
    public double getMaxDistance() {
        return MAX_DISTANCE;
    }

    @Override
    public double getMuzzleVelocity() {
        return BULLET_SPEED;
    }

    @Override
    public double getTimeBetweenShots() {
        return BASE_SHOT_TIME_BETWEEN; //TODO: see nextShotAtMs
    }

    @Override
    public double getShotsLeft() {
        return shotsLeft;
    }

    @Override
    public boolean canAimAt(D3Vector pos) {
        return true; //TODO
    }

    /**
     *
     * @param direction Direction to shoot at
     */
    @Override
    public void aimAndFireAt(D3Vector direction) {
        long currentTimeMs = System.currentTimeMillis();

        if (currentTimeMs >= nextShotAtMs && shotsLeft > 0) {

            //TODO: timeValidity, droneIdentifier
            int timeValidity = 100000;
            UUID ownerUUID = new UUID(42,42); //drone.getIdentifier();
            D3Vector initialPosition = gps.getLatestPlatformInfo().getPosition();
            D3Vector initialVelocity = direction.scale(BULLET_SPEED / direction.length());
            BulletEvent msg = new BulletEvent(timeValidity, ownerUUID, initialPosition, initialVelocity);

//            try {
                //TODO(PubSub)
//                publisher.send(MessageTopic.MOVEMENTS, msg);
                lastShotAtMs = currentTimeMs;
                nextShotAtMs = lastShotAtMs + BASE_SHOT_TIME_BETWEEN + new Random().nextInt(MAX_OFFSET_SHOT_TIME);
                shotsLeft--;
//            } catch (IOException e) {
//                log.fatal(e);
//            }
            //TODO: Run all the callbacks
            // callbacks.forEach(callback -> callback.run(msg));
            log.log(LogService.LOG_INFO, "Firing bullet in direction " + direction + "! Next shot possible in " + ((double) (nextShotAtMs - currentTimeMs) / 1000) + " seconds.");
        }
    }

    @Override
    public double getTimeToAimAt(D3Vector pos) {
        return BASE_AIM_TIME; //TODO: base time on `pos`
    }

    public void start() {
        log.log(LogService.LOG_INFO, "Starting Gun Component");
        //TODO start gun
    }

    public void stop() {
        log.log(LogService.LOG_INFO, "Stopping Gun Component");
        //TODO stop gun
    }
}
