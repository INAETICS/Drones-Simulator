package org.inaetics.dronessimulator.common;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.framework.util.StringComparator;
import org.apache.felix.framework.util.StringMap;
import org.apache.felix.main.AutoProcessor;
import org.apache.felix.main.Main;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.util.*;

public class FelixEmbeddedTest {
    /*https://svn.apache.org/repos/asf/felix/releases/org.apache.felix.main-1.2.0/doc/launching-and-embedding-apache-felix.html*/

    /*@Test
    public void startAndStopFelix() throws BundleException {

        HashMap<String, String> config = new HashMap<>();
        Felix felixBundle = new Felix(config);


        try {
            felixBundle.start();
            Assert.assertEquals(felixBundle.getState(), Felix.ACTIVE);
            felixBundle.stop();
            felixBundle.waitForStop(5000);

            Assert.assertNotEquals(felixBundle.getState(), Felix.ACTIVE);
            System.out.println("felix stopped.");
            felixBundle.uninstall();

        } catch (BundleException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }*/


    @Test
    public void startConfiguredFelix() throws BundleException {
        {
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
                List processors = new ArrayList();
                Map<String, String> configProps = Main.loadConfigProperties();
                if(configProps == null){
                    configProps = new HashMap<>();
                }
                processors.add(new AutoProcessor());
                Map felixConfig = new StringMap(configProps);
                Felix felix = new Felix(config);//, processors);
                felix.start();

//                felix.getBundleContext().installBundle();
//                felix.getBundleContext().registerService(, , )

                for (ServiceReference ref : felix.getBundle().getRegisteredServices()){
                    System.out.println("registered service: " + ref.toString());
                }
            } catch (Exception ex) {
                System.err.println("Could not create framework: " + ex);
                ex.printStackTrace();
                System.exit(-1);
            }
        }
    }
}
