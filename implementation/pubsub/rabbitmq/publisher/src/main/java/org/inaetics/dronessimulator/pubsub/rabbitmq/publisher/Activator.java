package org.inaetics.dronessimulator.pubsub.rabbitmq.publisher;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
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
        // TODO: Set up proper connection instead of defaults
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.newConnection();

        RabbitPublisher publisher = new RabbitPublisher(connection);

        manager.add(createComponent()
                .setInterface(Publisher.class.getName(), null)
                .setImplementation(publisher)
                .add(createServiceDependency()
                        .setService(Serializer.class)
                        .setRequired(true)
                ).setCallbacks("init", "connect", "disconnect", "destroy") // Init and destroy do not actually exist
        );
    }
}
