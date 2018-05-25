package org.inaetics.dronessimulator.drone.components.engine;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.pubsub.api.pubsub.Publisher;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        dependencyManager.add(createComponent()
                .setInterface(org.inaetics.dronessimulator.drone.components.engine.Engine.class.getName(), null)
                .setImplementation(Engine.class)
                .add(createServiceDependency()
                        .setService(DroneInit.class)
                        .setRequired(true)
                )
                .add(createServiceDependency()
                        .setService(Publisher.class,
                                String.format("(%s=%s)", Publisher.PUBSUB_TOPIC, MessageTopic.ALL))
                        .setRequired(true)
                )
                .add(createServiceDependency()
                        .setService(GPS.class)
                        .setRequired(true)
                )
                .setCallbacks("init", "start", "stop", "destroy")
        );
    }
}
