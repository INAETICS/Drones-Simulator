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

package org.inaetics.drone.simulator.components;
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

import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.drone.simulator.api.Constants;
import org.inaetics.drone.simulator.api.drone.DroneTactic;
import org.inaetics.drone.simulator.api.engine.Engine;
import org.inaetics.drone.simulator.api.gps.Gps;
import org.inaetics.drone.simulator.api.gun.Gun;
import org.inaetics.drone.simulator.api.radar.DetectionListener;
import org.inaetics.drone.simulator.api.radar.Radar;
import org.inaetics.drone.simulator.components.drone.DroneManager;
import org.inaetics.drone.simulator.components.engine.EngineImpl;
import org.inaetics.drone.simulator.components.gps.GpsImpl;
import org.inaetics.drone.simulator.components.gun.GunImpl;
import org.inaetics.drone.simulator.components.radar.RadarImpl;
import org.inaetics.drone.simulator.spi.costs.ComponentCost;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import static org.apache.felix.service.command.CommandProcessor.COMMAND_FUNCTION;
import static org.apache.felix.service.command.CommandProcessor.COMMAND_SCOPE;

/**
 * Note this class is based on the Felix Dependency manager
 * instead of a 'naked' bundle activator.
 * see http://felix.apache.org/documentation/subprojects/apache-felix-dependency-manager.html
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext ctx, org.apache.felix.dm.DependencyManager dm) throws Exception {
        String teamName = ctx.getProperty(Constants.DRONE_TEAM_NAME);
        System.out.println("init() with "+teamName);
        if (teamName == null) {
            teamName = "team";
        }

        //Creating DroneManager. responsible to announce drones to the game engine
        DroneManager mgmt = new DroneManager(teamName);
        Properties droneMgmtProps = new Properties();
        droneMgmtProps.setProperty("osgi.command.scope", "drone"); //NOTE not using constants in CommandProcessor to prevent class dependency on that interface
        droneMgmtProps.setProperty("osgi.command.function","cost");
        Component droneMgmtCmp = dm.createComponent()
                .setImplementation(mgmt) //lazy initialization
                .setInterface(DroneManager.class.getName(), droneMgmtProps) //NOTE setting impl as interface to enable providing command properties
                .add(dm.createServiceDependency()
                    .setService(LogService.class))
                .add(dm.createServiceDependency()
                    .setService(DroneTactic.class)
                    .setRequired(true))
                .add(dm.createServiceDependency()
                    .setService(ComponentCost.class)
                    .setCallbacks("addComponentCost", "removeComponentCost"));
        //TODO add service dep to MissionInfo
        dm.add(droneMgmtCmp); //drone manager always added


        //Creating Gun components (top and bottom mounted)
        String[] gunServices = new String[] {Gun.class.getName(), ComponentCost.class.getName()};

        GunImpl topGun = new GunImpl(GunImpl.MountLocation.TOP);
        Properties topGunProps = new Properties();
        topGunProps.setProperty("mount.location", GunImpl.MountLocation.TOP.toString());
        Component topGunCmp = dm.createComponent()
                .setImplementation(topGun)
                .setInterface(gunServices, topGunProps)
                 .add(dm.createServiceDependency()
                    .setService(Gps.class))
                .add(dm.createServiceDependency()
                    .setService(LogService.class));

        Properties bottomGunProps = new Properties();
        bottomGunProps.setProperty("mount.location", GunImpl.MountLocation.BOTTOM.toString());
        GunImpl bottomGun = new GunImpl(GunImpl.MountLocation.BOTTOM);
        Component bottomGunCmp = dm.createComponent()
                .setImplementation(bottomGun)
                .setInterface(gunServices, bottomGunProps)
                .add(dm.createServiceDependency()
                        .setService(Gps.class))
                .add(dm.createServiceDependency()
                        .setService(LogService.class));


        //Creating Radar component
        Properties radarProps = new Properties();
        String[] radarInterfaces = new String[]{Radar.class.getName(), ComponentCost.class.getName()};
        //TODO enable lines (and update imports) if the INAETICS pubsub maven dep is added
        //String[] radarInterfaces = new String[]{Radar.class.getName(), Subscriber.class.getName()};
        //radarProps.setProperty(PUBSUB_TOPIC, Constants.STATE_UPDATE_TOPIC_NAME);
        Component radarCmp = dm.createComponent()
                .setImplementation(RadarImpl.class)
                .setInterface(radarInterfaces, radarProps)
                .add(dm.createServiceDependency()
                    .setService(LogService.class))
                .add(dm.createServiceDependency()
                    .setService(DetectionListener.class)
                    .setCallbacks("addListener", "removeListener"));

        //Creating Gps component
        Properties gpsProps = new Properties();
        String[] gpsInterfaces = new String[]{Gps.class.getName(), ComponentCost.class.getName()};
        //TODO enable lines (and update imports) if the INAETICS pubsub maven dep is added
        //String[] radarInterfaces = new String[]{Radar.class.getName(), Subscriber.class.getName()};
        //radarProps.setProperty(PUBSUB_TOPIC, Constants.STATE_UPDATE_TOPIC_NAME);
        Component gpsCmp = dm.createComponent()
                .setImplementation(GpsImpl.class)
                .setInterface(gpsInterfaces, gpsProps)
                .add(dm.createServiceDependency()
                    .setService(LogService.class));

        addComponentIfEnabled(dm, topGunCmp, Constants.DRONE_COMPOMENTS_GUN_TOP_ENABLED);
        addComponentIfEnabled(dm, bottomGunCmp, Constants.DRONE_COMPONENTS_GUN_BOTTOM_ENABLED);
        addComponentIfEnabled(dm, radarCmp, Constants.DRONE_COMPONENTS_RADAR_ENABLED);
        addComponentIfEnabled(dm, gpsCmp, Constants.DRONE_COMPONENTS_GPS_ENABLED);
    }

    private void addComponentIfEnabled(DependencyManager dm, Component cmp, String enableKey) {
        BundleContext ctx = dm.getBundleContext();
        if ("true".equalsIgnoreCase(ctx.getProperty(enableKey))) {
            dm.add(cmp);
        }
    }
}
