package org.inaetics.dronessimulator.gameengine.physicsenginedriver;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.physicsengine.IPhysicsEngine;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        dependencyManager.add(createComponent()
            .setInterface(IPhysicsEngineDriver.class.getName(), null)
            .setImplementation(PhysicsEngineDriver.class)
            .add(createServiceDependency()
                .setService(IPhysicsEngine.class)
                .setRequired(true)
            )
            .add(createServiceDependency()
                .setService(IGameStateManager.class)
                .setRequired(true)
            )
            .add(createServiceDependency()
                .setService(IdentifierMapper.class)
                .setRequired(true)
            )
        );
    }
}
