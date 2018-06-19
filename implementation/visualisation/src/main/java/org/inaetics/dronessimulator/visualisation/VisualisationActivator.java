package org.inaetics.dronessimulator.visualisation;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.shell.DMCommand;
import org.apache.felix.gogo.command.Inspect;
import org.apache.felix.gogo.command.Util;
import org.inaetics.pubsub.api.pubsub.Publisher;
import org.inaetics.pubsub.api.pubsub.Subscriber;
import org.inaetics.pubsub.impl.pubsubadmin.zeromq.ZmqPublisher;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class VisualisationActivator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {

        final String TOPIC = "All";
        Properties subscriberProperties = new Properties();
        subscriberProperties.setProperty(Subscriber.PUBSUB_TOPIC, TOPIC);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /*NOTE: Originally, GameEngine subscribed to MessageTopic.MOVEMENTS and MessageTopic.STATEUPDATES */
            dependencyManager.add(createComponent()
                    .setInterface(Subscriber.class.getName(), subscriberProperties)
                    .setImplementation(Game.class)
                    .add(createServiceDependency()
                            .setService(Publisher.class ,"(" + Publisher.PUBSUB_TOPIC +"=" + TOPIC + ")")
                            .setRequired(true)
                    )
            );

            List<String> namespace = Util.parseSubstring("service");
//            Inspect.printRequirements(bundleContext, namespace, bundleContext.getBundles());
//            Inspect.printCapabilities(bundleContext, namespace, bundleContext.getBundles());

            System.out.println("dm.wtf() output:");
            new DMCommand(bundleContext).wtf();

        }).start();

//        Game.launch() //;
    }
}
