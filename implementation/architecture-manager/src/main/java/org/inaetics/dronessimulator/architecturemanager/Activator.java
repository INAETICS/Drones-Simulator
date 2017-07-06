package org.inaetics.dronessimulator.architecturemanager;


import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        // Register discoverer service
        manager.add(createComponent()
                .setImplementation(ArchitectureManager.class)
                .add(createServiceDependency()
                        .setService(Discoverer.class)
                        .setRequired(true)
                )
                .add(createServiceDependency()
                        .setService(Subscriber.class)
                        .setRequired(true)
                )
        );
    }
}
