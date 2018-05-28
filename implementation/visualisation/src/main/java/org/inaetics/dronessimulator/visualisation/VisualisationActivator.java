package org.inaetics.dronessimulator.visualisation;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.pubsub.api.pubsub.Publisher;
import org.inaetics.pubsub.api.pubsub.Subscriber;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class VisualisationActivator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {

        System.out.println("\nVisualisation Activator::init() ----\n");

        final String TOPIC = "test";
        Properties subscriberProperties = new Properties();
        subscriberProperties.setProperty(Subscriber.PUBSUB_TOPIC, TOPIC);

        dependencyManager.add(createComponent()
                .setInterface(Object.class.getName(),null)
                .setImplementation(new Vector()));

        /*NOTE: Originally, GameEngine subscribed to MessageTopic.MOVEMENTS and MessageTopic.STATEUPDATES */
        dependencyManager.add(createComponent()
                .setInterface(Subscriber.class.getName(), subscriberProperties)
                .setImplementation(Game.class)
                        .add(createServiceDependency()
                                .setService(Publisher.class ,"(" + Publisher.PUBSUB_TOPIC +"=" + TOPIC + ")")
                                .setRequired(true)
                        )
        );


    }
}
