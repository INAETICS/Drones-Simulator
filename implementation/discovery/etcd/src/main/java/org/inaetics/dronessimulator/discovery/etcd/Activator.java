package org.inaetics.dronessimulator.discovery.etcd;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;

import java.net.URI;

/**
 * Activator for the etcd discovery bundle.
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        // Register discoverer service
        manager.add(createComponent()
                .setInterface(Discoverer.class.getName(), null)
                .setImplementation(EtcdService.class)
                .add(createServiceDependency()
                        .setService(ConfigurationAdmin.class)
                        .setRequired(true)
                )
        );
    }
}
