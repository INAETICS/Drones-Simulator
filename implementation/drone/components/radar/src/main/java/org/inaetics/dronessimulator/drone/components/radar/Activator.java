package org.inaetics.dronessimulator.drone.components.radar;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.osgi.framework.BundleContext;


public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        dependencyManager.add(createComponent()
                .setInterface(Radar.class.getName(), null)
                .setImplementation(Radar.class)
                .add(createServiceDependency()
                        .setService(DroneInit.class)
                        .setRequired(true)
                )
                .add(createServiceDependency()
                        .setService(Subscriber.class)
                        .setRequired(true)
                )
                .add(createServiceDependency()
                        .setService(Discoverer.class)
                        .setRequired(true)
                )
                .add(createServiceDependency()
                        .setService(ArchitectureEventController.class)
                        .setRequired(true)
                )
        );
    }
}