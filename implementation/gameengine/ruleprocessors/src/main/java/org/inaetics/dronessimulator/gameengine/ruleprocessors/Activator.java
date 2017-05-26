package org.inaetics.dronessimulator.gameengine.ruleprocessors;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        dependencyManager.add(
            createComponent()
            .setInterface(IRuleProcessors.class.getName(), null)
            .setImplementation(RuleProcessors.class)
            .setCallbacks("init", "start", "quit", "destroy")
            .add(createServiceDependency()
                 .setService(IPhysicsEngineDriver.class)
                 .setRequired(true)
             )
             .add(createServiceDependency()
                  .setService(Publisher.class)
                  .setRequired(true)
             )
             .add(createServiceDependency()
                  .setService(IdentifierMapper.class)
                  .setRequired(true)
             )

        );
    }
}
