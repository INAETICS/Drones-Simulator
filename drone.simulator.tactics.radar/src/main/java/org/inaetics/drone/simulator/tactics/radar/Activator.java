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

package org.inaetics.drone.simulator.tactics.radar;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyActivatorBase;
import org.inaetics.drone.simulator.api.drone.DroneTactic;
import org.inaetics.drone.simulator.api.engine.Engine;
import org.inaetics.drone.simulator.api.gps.Gps;
import org.inaetics.drone.simulator.api.radar.DetectionListener;
import org.inaetics.drone.simulator.api.radar.Radar;
import org.inaetics.drone.simulator.api.radio.Radio;
import org.inaetics.drone.simulator.api.radio.RadioMessageListener;
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

        final String teamName = "team1";
        Properties props = new Properties();
        props.setProperty("team", teamName);

        String[] interfaces = new String[]{DroneTactic.class.getName(), DetectionListener.class.getName(), RadioMessageListener.class.getName()};

        //Creating RadarTactic component
        Component cmp = dm.createComponent()
                .setImplementation(RadarTacticImpl.class)
                .setInterface(interfaces, props)
                .add(dm.createServiceDependency()
                        .setRequired(true)
                        .setService(Engine.class))
                .add(dm.createServiceDependency()
                        .setRequired(true)
                        .setService(Gps.class))
                .add(dm.createServiceDependency()
                        .setRequired(true)
                        .setService(Radio.class))
                .add(dm.createServiceDependency()
                        .setRequired(true)
                        .setService(Radar.class))
                .add(dm.createServiceDependency()
                        .setService(LogService.class));

        dm.add(cmp);
    }
}
