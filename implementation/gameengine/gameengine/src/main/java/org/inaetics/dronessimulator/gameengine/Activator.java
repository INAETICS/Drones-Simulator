package org.inaetics.dronessimulator.gameengine;


import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.GameStateManager;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.PhysicsEngineDriver;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.IRuleProcessors;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.RuleProcessors;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;

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
                .setService(ConfigurationAdmin.class)
                .setRequired(true)
            )
        );
    }
}

/**
 * TODO: Refactor all driver code to seperate physicsengine driver module
 *       Make sure physicsenginemessage do not use any physicsengine imports (maybe even rename to GameMessages?)
 *       Refactor all ruleprocessors to seperate module
 *       Make GameStateManager update physicsengine upon adding, changing or removing GameEntity (watch concurrency!)
 *       Documentation
 *       Tests
 */
