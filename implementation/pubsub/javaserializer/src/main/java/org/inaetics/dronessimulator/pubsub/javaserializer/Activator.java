package org.inaetics.dronessimulator.pubsub.javaserializer;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;
import org.osgi.framework.BundleContext;

/**
 * Activator for the Java serializer implementation.
 */
public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        // Register service
        manager.add(createComponent()
                .setInterface(Serializer.class.getName(), null)
                .setImplementation(JavaSerializer.class)
        );
    }
}
