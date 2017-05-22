package org.inaetics.dronessimulator.gameengine;


import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.IRuleProcessors;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        dependencyManager.add(createComponent()
            .setImplementation(GameEngine.class)
            .add(createServiceDependency()
                .setService(Subscriber.class)
                .setRequired(true)
            )
            .add(createServiceDependency()
                .setService(IGameStateManager.class)
                .setRequired(true)
            )
            .add(createServiceDependency()
                .setService(IPhysicsEngineDriver.class)
                .setRequired(true)
            )
            .add(createServiceDependency()
                .setService(IRuleProcessors.class)
                .setRequired(true)
            )
            .add(createServiceDependency()
                .setService(IdentifierMapper.class)
                .setRequired(true)
            )
        );
    }
}
