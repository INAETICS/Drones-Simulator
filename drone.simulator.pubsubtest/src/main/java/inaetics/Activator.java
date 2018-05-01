package inaetics;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.pubsub.api.pubsub.Publisher;
import org.osgi.framework.BundleContext;

import java.util.Dictionary;
import java.util.Hashtable;

public class Activator extends DependencyActivatorBase {

    private final static String TOPIC = "test";

    @Override
    public void init(BundleContext bundleContext, DependencyManager dm) throws Exception {
        String[] objectClass = new String[] {Object.class.getName()};
        Dictionary<String, Object> properties = new Hashtable<>();

        System.out.println("Activator.init()");
        dm.add(
                dm.createComponent()
                        .setInterface(objectClass, properties)
                        .setImplementation(Demo.class)
                        .add(createServiceDependency()
                                .setService(Publisher.class ,"(" + Publisher.PUBSUB_TOPIC +"=" + TOPIC + ")")
                                .setRequired(true))
        );
    }
}
