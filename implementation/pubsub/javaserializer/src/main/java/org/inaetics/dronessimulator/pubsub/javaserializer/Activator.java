package org.inaetics.dronessimulator.pubsub.javaserializer;

import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator for the Java serializer implementation.
 */
public class Activator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        context.registerService(Serializer.class, new JavaSerializer(), null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        // We don't have to destruct anything here
    }
}
