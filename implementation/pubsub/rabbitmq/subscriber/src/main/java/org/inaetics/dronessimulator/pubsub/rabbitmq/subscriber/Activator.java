package org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber;

import com.rabbitmq.client.ConnectionFactory;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.osgi.framework.BundleContext;

/**
 * Activator for the RabbitMQ subscriber implementation.
 */
public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        // TODO: Set up proper connection instead of defaults
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("rabbitmq");

        // TODO: Make name configurable
        RabbitSubscriber subscriber = new RabbitSubscriber(connectionFactory, "defaultSubscriber");

        manager.add(createComponent()
                .setInterface(Subscriber.class.getName(), null)
                .setImplementation(subscriber)
                .add(createServiceDependency()
                        .setService(Serializer.class)
                        .setRequired(true)
                ).setCallbacks("init", "connect", "disconnect", "destroy") // Init and destroy do not actually exist
        );
    }
}
