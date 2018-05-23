package org.inaetics.dronessimulator.drone.components.radar;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.pubsub.api.pubsub.Subscriber;
import org.osgi.framework.BundleContext;

import java.util.Properties;


public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        String[] interfaces = new String[]{Radar.class.getName(), Subscriber.class.getName()};
        Properties properties = new Properties();
        properties.setProperty(Subscriber.PUBSUB_TOPIC, MessageTopic.STATEUPDATES.getName());
        dependencyManager.add(createComponent()
                .setInterface(interfaces, properties)
                .setImplementation(Radar.class)
                .add(createServiceDependency()
                        .setService(DroneInit.class)
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