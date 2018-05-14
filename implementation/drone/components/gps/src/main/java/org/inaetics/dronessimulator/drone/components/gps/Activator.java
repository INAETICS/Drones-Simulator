package org.inaetics.dronessimulator.drone.components.gps;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.pubsub.api.pubsub.Subscriber;
import org.osgi.framework.BundleContext;

/**
 * Created by mart on 17-5-17.
 */
public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        String subscriberProps = String.format("(%s=%s)", Subscriber.PUBSUB_TOPIC, MessageTopic.STATEUPDATES);
        dependencyManager.add(createComponent()
                .setInterface(GPS.class.getName(), null)
                .setImplementation(GPS.class)
                .add(createServiceDependency()
                        .setService(DroneInit.class)
                        .setRequired(true)
                )
                .add(createServiceDependency()
                        .setService(Subscriber.class, subscriberProps)
                        .setRequired(true)
                ).setCallbacks("init", "start", "stop", "destroy")
        );
    }
}
