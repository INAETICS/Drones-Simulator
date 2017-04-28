package org.inaetics.dronessimulator.physicsenginewrapper;


import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        dependencyManager.add(createComponent()
            .setImplementation(PhysicsEngineWrapper.class)
            .add(createServiceDependency()
                .setService(Publisher.class)
                .setRequired(true)
            )
            .add(createServiceDependency()
                .setService(Subscriber.class)
                .setRequired(true)
            )
        );
    }

    @Override
    public void destroy(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        Component component = dependencyManager.getComponents().get(0);

        if(component != null) {
            PhysicsEngineWrapper bundle = component.getInstance();

            if(bundle != null) {
                bundle.stop();
            }
        }
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
