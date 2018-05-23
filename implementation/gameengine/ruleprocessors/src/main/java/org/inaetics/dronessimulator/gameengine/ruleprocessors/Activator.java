package org.inaetics.dronessimulator.gameengine.ruleprocessors;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.pubsub.api.pubsub.Publisher;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        System.out.println("--------------------\n\nruleprocessors::init");
        final String TOPIC = "test";
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
                                .setService(Publisher.class, "(" + Publisher.PUBSUB_TOPIC + "=" + TOPIC + ")")
                                .setRequired(true)
                        )
                        .add(createServiceDependency()
                                .setService(IdentifierMapper.class)
                                .setRequired(true)
                        )
                        .add(createServiceDependency()
                                .setService(ArchitectureEventController.class)
                                .setRequired(true)
                        )

        );
    }
}
