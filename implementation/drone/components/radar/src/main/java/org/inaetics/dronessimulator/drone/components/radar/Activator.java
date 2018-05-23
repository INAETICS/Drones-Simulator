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
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("STARTED " + this.getClass().getName());
            dependencyManager.add(createComponent()
                    .setInterface(interfaces, properties)
                    .setImplementation(Radar.class)
                    .add(createServiceDependency()
                            .setService(DroneInit.class)
                            .setRequired(false)
                    )
                    .add(createServiceDependency()
                            .setService(Discoverer.class)
                            .setRequired(false)
                    )
                    .add(createServiceDependency()
                            .setService(ArchitectureEventController.class)
                            .setRequired(false)
                    )
            );
        }).start();
    }
}