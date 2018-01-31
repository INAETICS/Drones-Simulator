package org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.osgi.framework.BundleContext;

/**
 * Activator for the RabbitMQ subscriber implementation.
 */
public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        manager.add(createComponent()
                .setInterface(Subscriber.class.getName(), null)
                .setImplementation(RabbitSubscriber.class)
                .add(createServiceDependency()
                        .setService(Serializer.class)
                        .setRequired(true))
                .add(createServiceDependency()
                        .setService(Discoverer.class)
                        .setRequired(true))
                .add(createConfigurationDependency()
                        .setPid("rabbitmq.broker.default")
                        .setRequired(true)
                        .setCallback("setConfig"))
                .setCallbacks("init", "connect", "disconnect", "destroy") // Init and destroy do not actually exist
        );
    }
}
