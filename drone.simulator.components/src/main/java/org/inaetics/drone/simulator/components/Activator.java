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
import org.inaetics.drone.simulator.api.gun.Gun;
import org.inaetics.drone.simulator.api.radar.DetectionListener;
import org.inaetics.drone.simulator.api.radar.Radar;
import org.inaetics.drone.simulator.components.gun.GunImpl;
import org.inaetics.drone.simulator.components.radar.RadarImpl;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

import java.util.Properties;

/**
 * Note this class is based on the Felix Dependency manager
 * instead of a 'naked' bundle activator.
 * see http://felix.apache.org/documentation/subprojects/apache-felix-dependency-manager.html
 */
public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext ctx, org.apache.felix.dm.DependencyManager dm) throws Exception {
        //TODO add configuration dependencies to control availability of drone components

        //Creating Gun component
        Component gunCmp = dm.createComponent()
                .setImplementation(GunImpl.class)
                .setInterface(Gun.class.getName(), null)
                .add(dm.createServiceDependency()
                    .setService(LogService.class));

        //Creating Radar component
        Properties radarProps = new Properties();
        String[] radarInterfaces = new String[]{Radar.class.getName()};
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

        dm.add(gunCmp);
        dm.add(radarCmp);
    }
}
