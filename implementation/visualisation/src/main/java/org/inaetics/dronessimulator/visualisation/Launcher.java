package org.inaetics.dronessimulator.visualisation;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.cache.BundleCache;
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

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Launcher {

    public static void main(String[] args)

    {
        System.out.println("Game Launcher...");
        System.out.println("Starting Felix");


        String framework_systempackages = null;
        try (Stream<String> stream = Files.lines(Paths.get("framework_systempackages.txt"))) {
            framework_systempackages = stream.filter(l -> !l.startsWith("//")).collect(Collectors.joining(","));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map config = new org.eclipse.jetty.util.StringMap(true);
        Main.copySystemProperties(config);
        System.out.println("Constants.FRAMEWORK_SYSTEMPACKAGES := " + framework_systempackages);
        config.put(Constants.FRAMEWORK_SYSTEMPACKAGES, framework_systempackages);

        config.put(AutoProcessor.AUTO_START_PROP + ".1",
                "file:bundle/org.apache.felix.shell-1.0.0.jar " +
                        "file:bundle/org.apache.felix.shell.tui-1.0.0.jar");
        config.put(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT); //clean cache
//        config.put(BundleCache.CACHE_ROOTDIR_PROP, "willem-cache");

        try {


            List activators = new ArrayList<BundleActivator>();
            activators.add(new VisualisationActivator());
            config.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, activators);


            Map<String, String> configProps = Main.loadConfigProperties();
            if (configProps == null) {
                configProps = new HashMap<>();
            }

            config.putAll(configProps);
            config.putAll(config);

            Felix felix = new Felix(config);//, processors);

            felix.init();

            AutoProcessor.process(configProps, felix.getBundleContext());

            String jar_path = new File(Launcher.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile().getAbsolutePath();
            String dependencies_uri = "file:" + jar_path + "/bundle_dependencies/";


            String[] bundle_filesnames = null;
            try (Stream<String> stream = Files.lines(Paths.get("bundles_list.txt"))) {
                bundle_filesnames = stream.filter(l -> !l.startsWith("//")).toArray(String[]::new);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (String path : bundle_filesnames) {
                String bundle_path = dependencies_uri + path;
                Bundle bundle = felix.getBundleContext().installBundle(bundle_path);
                System.out.println("Installed bundle: " + bundle.toString() + " ---- state: " + bundle.getState() + "\tpath="+path);
            }

            ServiceReference<?>[] registeredServices = felix.getBundleContext().getBundle().getRegisteredServices();

            for (ServiceReference ref : registeredServices) {
                System.out.println("registered service: " + ref.toString());
            }


//            final String TOPIC = "test";
//            Dictionary subscriberProperties = new Properties();
//            subscriberProperties.put(Subscriber.PUBSUB_TOPIC, TOPIC);
 //            ServiceRegistration registration = felix.getBundleContext().registerService(Subscriber.class.getName(), new Game(), subscriberProperties);

            felix.start();

            System.out.println("List bundles:");
            for (Bundle bundle : felix.getBundleContext().getBundles()) {
                bundle.start();
                System.out.println("Started bundle: " + bundle.toString() + " \tVersion: " + bundle.getVersion() + " \tstate: " + bundle.getState());

            }

            List<String> namespace = Util.parseSubstring("service");
            System.out.println(namespace.size());
//            Inspect.printRequirements(felix.getBundleContext(), namespace, felix.getBundleContext().getBundles());
//            Inspect.printCapabilities(felix.getBundleContext(), namespace, felix.getBundleContext().getBundles());

//            felix.stop();
//            felix.waitForStop(2000);


        } catch (Exception ex) {
            System.err.println("Could not create framework: " + ex);
            ex.printStackTrace();
            System.exit(-1);
        }
    }

}