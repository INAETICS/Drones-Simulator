package org.inaetics.dronessimulator.drone.components.gps;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.pubsub.api.pubsub.Subscriber;
import org.osgi.framework.BundleContext;

import java.util.Properties;

/**
 * Created by mart on 17-5-17.
 */
public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        Properties properties = new Properties();
        properties.setProperty(Subscriber.PUBSUB_TOPIC, MessageTopic.STATEUPDATES.getName());
        String[] interfaces = new String[]{Subscriber.class.getName(), GPS.class.getName()};
        dependencyManager.add(createComponent()
                .setInterface(interfaces, properties)
                .setImplementation(GPS.class)
                .add(createServiceDependency()
                        .setService(DroneInit.class)
                        .setRequired(true)
                ).setCallbacks("init", "start", "stop", "destroy")
        );
    }
}
