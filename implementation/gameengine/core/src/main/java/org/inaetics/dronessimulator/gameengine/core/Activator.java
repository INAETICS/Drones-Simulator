package org.inaetics.dronessimulator.gameengine.core;


import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.IRuleProcessors;
import org.inaetics.pubsub.api.pubsub.Subscriber;
import org.osgi.framework.BundleContext;

import java.util.Properties;

public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {

        System.out.println("\n\n---------------- Gameengine Activator::init() ----- \n\n");

        final String TOPIC = "test";
        Properties subscriberProperties = new Properties();
        subscriberProperties.setProperty(Subscriber.PUBSUB_TOPIC, TOPIC);

        /*NOTE: Originally, GameEngine subscribed to MessageTopic.MOVEMENTS and MessageTopic.STATEUPDATES */
        dependencyManager.add(createComponent()
            .setInterface(new String[]{Subscriber.class.getName()}, subscriberProperties)
            .setImplementation(GameEngine.class)
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
            .add(createServiceDependency()
                .setService(Discoverer.class)
                .setRequired(true)
            )
            .add(createServiceDependency()
                .setService(ArchitectureEventController.class)
                .setRequired(true)
            )
        );
    }
}
