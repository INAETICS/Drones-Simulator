package org.inaetics.dronessimulator.discovery.etcd;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.osgi.framework.BundleContext;

import java.net.URI;

/**
 * Activator for the etcd discovery bundle.
 */
public class Activator extends DependencyActivatorBase {
    /** Config discoverer thread. */
    private Thread configDiscoverer;

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        // TODO: Make this URI dynamic
        EtcdDiscoverer discoverer = new EtcdDiscoverer(URI.create("http://localhost:4001/"));

        // Register discoverer service
        manager.add(createComponent()
                .setInterface(Discoverer.class.getName(), null)
                .setImplementation(discoverer)
                .setCallbacks("init", "registerAll", "unregisterAll", "destroy")
        );

        // Run config discoverer
        EtcdConfigDiscoverer configDiscoverer = new EtcdConfigDiscoverer(discoverer, manager);
        this.configDiscoverer = new Thread(configDiscoverer);
        this.configDiscoverer.start();
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // Stop config discoverer thread
        this.configDiscoverer.interrupt();

        super.destroy(context, manager);
    }
}
