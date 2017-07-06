package org.inaetics.dronessimulator.physicsengine;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        dependencyManager.add(createComponent()
            .setInterface(IPhysicsEngine.class.getName(), null)
            .setImplementation(PhysicsEngine.class)
        );
    }
}
