package org.inaetics.dronessimulator.drone.components.gps;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.drone.components.Component;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.osgi.framework.BundleContext;

/**
 * Created by mart on 17-5-17.
 */
public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        dependencyManager.add(createComponent()
                .setImplementation(GPS.class)
                .add(createServiceDependency()
                        .setService(Subscriber.class)
                        .setRequired(true)
                ).add(createServiceDependency()
                        .setService(Component.class)
                        .setRequired(false)
                        .setCallbacks("addComponent", "removeComponent")
                ).setCallbacks("init", "start", "stop", "destroy")
        );
    }
}
