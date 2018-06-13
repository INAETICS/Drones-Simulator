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

            System.out.println("\n\t=====\tCLASSLOADER 1 = "+Publisher.class.getClassLoader());
            System.out.println("\n\t=====\tCLASSLOADER 1 ZMQ = "+ org.inaetics.pubsub.impl.pubsubadmin.zeromq.ZmqPublisher.class.getClassLoader());


            List<String> namespace = Util.parseSubstring("service");
//            Inspect.printRequirements(bundleContext, namespace, bundleContext.getBundles());
//            Inspect.printCapabilities(bundleContext, namespace, bundleContext.getBundles());

            System.out.println("WTF?");
            new DMCommand(bundleContext).wtf();


            // Create a service tracker to monitor dictionary services.
//            ServiceTracker tracker = null;
//            try {
//                tracker = new ServiceTracker(
//                        bundleContext,
//                        bundleContext.createFilter(
//                                "(objectClass=" + Publisher.class.getName() + ")"),
//                        null);
//                tracker.open();
//            } catch (InvalidSyntaxException e) {
//                e.printStackTrace();
//            }
//            ZmqPublisher tracker_publisher = (ZmqPublisher) tracker.getService();
//            System.out.println("Publisher from tracker = "+tracker_publisher);

        }).start();

//        Game.launch() //;
    }
}
