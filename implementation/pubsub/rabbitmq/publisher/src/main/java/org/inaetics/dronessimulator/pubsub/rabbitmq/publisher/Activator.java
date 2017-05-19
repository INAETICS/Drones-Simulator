package org.inaetics.dronessimulator.pubsub.rabbitmq.publisher;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;
import org.osgi.framework.BundleContext;

/**
 * Activator for the RabbitMQ publisher implementation.
 */
public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        RabbitPublisher publisher = new RabbitPublisher();

        manager.add(createComponent()
                .setInterface(Publisher.class.getName(), null)
                .setImplementation(publisher)
                .add(createServiceDependency()
                        .setService(Serializer.class)
                        .setRequired(true))
                .add(createConfigurationDependency()
                        .setPid("rabbitmq.broker.default")
                        .setRequired(true)
                        .setCallback("updateConfig"))
                .setCallbacks("init", "connect", "disconnect", "destroy") // Init and destroy do not actually exist
        );
    }
}
