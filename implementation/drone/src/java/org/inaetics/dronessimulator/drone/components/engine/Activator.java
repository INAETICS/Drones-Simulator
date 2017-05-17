package org.inaetics.dronessimulator.drone.components.engine;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.drone.DroneInit;
import org.inaetics.dronessimulator.drone.tactic.Tactic;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        dependencyManager.add(createComponent()
                .setInterface(Engine.class.getName(), null)
                .setImplementation(Engine.class)
                .add(createServiceDependency()
                        .setService(DroneInit.class)
                        .setRequired(true)
                )
                .add(createServiceDependency()
                        .setService(Publisher.class)
                        .setRequired(true)
                )
                .setCallbacks("init", "start", "stop", "destroy")
        );
    }
}
