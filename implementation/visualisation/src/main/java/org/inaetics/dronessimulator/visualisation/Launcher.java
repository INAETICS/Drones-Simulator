package org.inaetics.dronessimulator.visualisation;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.framework.util.StringMap;
import org.apache.felix.gogo.command.Inspect;
import org.apache.felix.gogo.command.Util;
import org.apache.felix.main.AutoProcessor;
import org.apache.felix.main.Main;
import org.inaetics.pubsub.api.pubsub.Subscriber;
import org.osgi.application.Framework;
import org.osgi.framework.*;
import org.osgi.framework.wiring.BundleWiring;

import java.util.*;

public class Launcher {

    /**
     * Main method of the visualisation
     *
     * @param args - args
     */
    public static void main(String[] args)

    {
        System.out.println("Game Launcher...");
        System.out.println("Starting Felix");
        Map config = new StringMap(); //NOTE: Elements should be case insensitive!
        config.put(Constants.FRAMEWORK_SYSTEMPACKAGES,
                "org.osgi.framework; version=1.3.0," +
                        "org.osgi.service.packageadmin; version=1.2.0," +
                        "org.osgi.service.startlevel; version=1.0.0," +
                        "org.osgi.service.url; version=1.0.0");
        config.put(AutoProcessor.AUTO_START_PROP + ".1",
                "file:bundle/org.apache.felix.shell-1.0.0.jar " +
                        "file:bundle/org.apache.felix.shell.tui-1.0.0.jar");
//        config.put(BundleCache.CACHE_PROFILE_DIR_PROP, "cache");

        try {


            // Create host activator;
            List activators = new ArrayList<BundleActivator>();
            activators.add(new VisualisationActivator());
            config.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, activators);


//            List processors = new ArrayList();
            Map<String, String> configProps = Main.loadConfigProperties();
            if (configProps == null) {
                configProps = new HashMap<>();
            }
//            processors.add(new AutoProcessor());
            Map felixConfig = new StringMap(configProps);
            Main.copySystemProperties(configProps);

            felixConfig.putAll(configProps);


            Felix felix = new Felix(config);//, processors);
            felix.init();

            AutoProcessor.process(configProps, felix.getBundleContext());

//            Framework m_fwk = getFrameworkFactory().newFramework(null);

            /*Add bundles here*/
            /*final String TOPIC = "test";
            Hashtable<String, String> subscriberProps = new Hashtable<String, String>();
            subscriberProps.put( Subscriber.PUBSUB_TOPIC, TOPIC);
            ServiceRegistration<?> gameSR = felix.getBundleContext().registerService(Game.class.getName(), new Game(), subscriberProps);*/

//            felix.getBundleContext().


            ServiceReference<?>[] registeredServices = felix.getBundleContext().getBundle().getRegisteredServices();
//            List<String> namespaces = new ArrayList<>();


            for (ServiceReference ref : registeredServices) {
//                namespaces.add(ref.toString());
                System.out.println("registered service: " + ref.toString());
            }


            /*
        osgi.identity;filter:='(osgi.identity=org.inaetics.pubsub.api)',\
        osgi.identity;filter:='(osgi.identity=org.inaetics.pubsub.psa.zeromq)',\
        osgi.identity;filter:='(osgi.identity=org.inaetics.pubsub.topologymanager)',\
        osgi.identity;filter:='(osgi.identity=org.inaetics.pubsub.discovery.etcd)',\
        osgi.identity;filter:='(osgi.identity=org.inaetics.pubsub.serialization.json)',\
        */
        felix.getBundleContext().installBundle("file:/home/witwu/dev2/pub-sub-admin-java/org.inaetics.pubsub.psa.zeromq/target/org.inaetics.pubsub.psa.zeromq-1.0.0-SNAPSHOT.jar");
        felix.getBundleContext().installBundle("file:/home/witwu/dev2/pub-sub-admin-java/org.inaetics.pubsub.api/target/org.inaetics.pubsub.api-1.0.0-SNAPSHOT.jar");
        felix.getBundleContext().installBundle("file:/home/witwu/dev2/pub-sub-admin-java/org.inaetics.pubsub.topologymanager/target/org.inaetics.pubsub.topologymanager-1.0.0-SNAPSHOT.jar");
        felix.getBundleContext().installBundle("file:/home/witwu/dev2/pub-sub-admin-java/org.inaetics.pubsub.discovery.etcd/target/org.inaetics.pubsub.discovery.etcd-1.0.0-SNAPSHOT.jar");
        felix.getBundleContext().installBundle("file:/home/witwu/dev2/pub-sub-admin-java/org.inaetics.pubsub.serialization.json/target/org.inaetics.pubsub.serialization.json-1.0.0-SNAPSHOT.jar");

            felix.start();

//            for (Bundle b : felix.getBundleContext().getBundles()) {
//                System.out.println("bundle: " + b.toString() + " ---- state: " + b.getState());
//                BundleWiring wiring = b.adapt(BundleWiring.class);
//                System.out.println(wiring.);

                List<String> namespace = Util.parseSubstring("service");
                System.out.println(namespace.size());
                Inspect.printRequirements(felix.getBundleContext(), namespace, felix.getBundleContext().getBundles());
                Inspect.printCapabilities(felix.getBundleContext(), namespace, felix.getBundleContext().getBundles());
//            }

//            felix.stop();
//            felix.waitForStop(2000);


        } catch (Exception ex) {
            System.err.println("Could not create framework: " + ex);
            ex.printStackTrace();
            System.exit(-1);
        }
    }

/*    private static class MyActivator extends DependencyActivatorBase {


        @Override
        public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
            System.out.println("Init GameActivator");

            final String TOPIC = "test";
            Hashtable<String, String> subscriberProps = new Hashtable<String, String>();
            subscriberProps.put( Subscriber.PUBSUB_TOPIC, TOPIC);
//            ServiceRegistration<?> gameSR = felix.getBundleContext().registerService(Game.class.getName(), new Game(), subscriberProps);

        }

        *//*@Override
        public void start(BundleContext bundleContext) throws Exception {

            // Game game = new Game();
            //   game.launch(args);

        }

        @Override
        public void stop(BundleContext bundleContext) throws Exception {
            System.out.println("STOP :( !!!!");

        }*//*
    }*/

}