package org.inaetics.dronessimulator.drone;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.drone.old.Drone;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        dependencyManager.add(createComponent()
                .setInterface(Drone.class.getName(), null)
                .setImplementation(DroneInit.class)
                .setCallbacks("init", "start", "stop", "destroy")
        );
    }
}