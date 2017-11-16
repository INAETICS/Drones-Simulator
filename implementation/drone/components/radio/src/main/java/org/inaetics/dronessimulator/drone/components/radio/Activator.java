package org.inaetics.dronessimulator.drone.components.radio;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.osgi.framework.BundleContext;

/**
 * Felix Dependency Manager activator for Radio Component.
 */
public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        dependencyManager.add(createComponent()
                .setInterface(Radio.class.getName(), null)
                .setImplementation(Radio.class)
                .add(createServiceDependency()
                        .setService(DroneInit.class)
                        .setRequired(true)
                )
                .add(createServiceDependency()
                        .setService(Publisher.class)
                        .setRequired(true)
                ).add(createServiceDependency()
                        .setService(Subscriber.class)
                        .setRequired(true)
                ).setCallbacks("init", "start", "stop", "destroy")
        );
    }
}
