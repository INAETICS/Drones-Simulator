package org.inaetics.dronessimulator.architecturemanager;


import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.pubsub.api.pubsub.Subscriber;
import org.osgi.framework.BundleContext;

import java.util.Properties;

public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        // Register discoverer service

        String[] interfaces = new String[]{Subscriber.class.getName()};
        Properties properties = new Properties();
        properties.setProperty(Subscriber.PUBSUB_TOPIC, MessageTopic.ALL.getName());
        manager.add(createComponent()
                .setImplementation(ArchitectureManager.class)
                .setInterface(interfaces, properties)
                .add(createServiceDependency()
                        .setService(Discoverer.class)
                        .setRequired(true)
                )
        );
    }
}
