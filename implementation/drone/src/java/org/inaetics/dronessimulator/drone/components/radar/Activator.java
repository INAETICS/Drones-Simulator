package org.inaetics.dronessimulator.drone.components.radar;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.drone.components.Component;
import org.osgi.framework.BundleContext;

/**
 * Created by mart on 15-5-17.
 */
public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        dependencyManager.add(createComponent()
                .setImplementation(SimpleRadar.class)
                .setCallbacks("init", "start", "stop", "destroy")
        );
    }
}