package org.inaetics.dronessimulator.drone.components.gun;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.osgi.framework.BundleContext;

/**
 * Created by mart on 17-5-17.
 */
public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        dependencyManager.add(createComponent()
                .setInterface(Gun.class.getName(), null)
                .setImplementation(Gun.class)
                .add(createServiceDependency()
                        .setService(DroneInit.class)
                        .setRequired(true)
                )
                .add(createServiceDependency()
                        .setService(Publisher.class)
                        .setRequired(true)
                )
                .add(createServiceDependency()
                        .setService(GPS.class)
                        .setRequired(true)
                )
                .setCallbacks("init", "start", "stop", "destroy")
        );
    }
}
