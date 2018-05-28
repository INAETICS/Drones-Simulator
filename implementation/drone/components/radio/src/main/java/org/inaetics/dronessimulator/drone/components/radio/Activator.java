package org.inaetics.dronessimulator.drone.components.radio;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.pubsub.api.pubsub.Publisher;
import org.inaetics.pubsub.api.pubsub.Subscriber;
import org.osgi.framework.BundleContext;

import java.util.Properties;

/**
 * Felix Dependency Manager activator for Radio Component.
 */
public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        Properties subscriberProperties = new Properties();
        subscriberProperties.setProperty(Subscriber.PUBSUB_TOPIC, MessageTopic.ALL.getName());
        new Thread(() ->
        {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dependencyManager.add(createComponent()
                    .setInterface(Subscriber.class.getName(), subscriberProperties)
                    .setImplementation(Radio.class)
                    .add(createServiceDependency()
                            .setService(DroneInit.class)
                            .setRequired(true)
                    )
                    .add(createServiceDependency()
                            .setService(Publisher.class)
                            .setRequired(true)
                    ).setCallbacks("init", "start", "stop", "destroy")
            );
            System.out.println("STARTED " + this.getClass().getName());
        }).start();
    }
}
