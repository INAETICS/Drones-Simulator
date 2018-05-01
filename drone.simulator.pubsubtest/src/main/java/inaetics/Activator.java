package inaetics;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.pubsub.api.pubsub.Publisher;
import org.inaetics.pubsub.api.pubsub.Subscriber;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;
import inaetics.subscriber.DemoSubscriber;

import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

//import static org.inaetics.pubsub.api.pubsub.Subscriber.PUBSUB_TOPIC;

public class Activator extends DependencyActivatorBase {

    private final static String TOPIC = "test";

    @Override
    public void init(BundleContext bundleContext, DependencyManager dm) throws Exception {
        String[] objectClass = new String[] {Object.class.getName()};
        Dictionary<String, Object> properties = new Hashtable<>();

        System.out.println("Activator.init()");

        Properties subscriberProperties = new Properties();
        subscriberProperties.setProperty(Subscriber.PUBSUB_TOPIC, TOPIC);
        System.out.println("subscriber props: " + subscriberProperties.toString());
        dm.add(createComponent()
                        .setImplementation(DemoSubscriber.class)
                        .setInterface(new String[]{Subscriber.class.getName()}, subscriberProperties)
                        .setCallbacks("init", "connect", "disconnect", "destroy")
        );

        dm.add(
                dm.createComponent()
                        .setInterface(objectClass, properties)
                        .setImplementation(DemoPublisher.class)
                        .add(createServiceDependency()
                                .setService(Publisher.class ,"(" + Publisher.PUBSUB_TOPIC +"=" + TOPIC + ")")
                                .setRequired(true))
        );

        // CONFIGURE PUB SUB
        // TODO: Figure out how to configure zeroMQ
        ServiceReference configurationAdminReference =
                bundleContext.getServiceReference(ConfigurationAdmin.class.getName());

        if (configurationAdminReference != null) {
            System.out.println("Configuring etcd and zmq...");

            ConfigurationAdmin confAdmin =
                    (ConfigurationAdmin) bundleContext.getService(configurationAdminReference);

            try {
                Configuration config = confAdmin
                        .getConfiguration("org.inaetics.pubsub.impl.discovery.etcd.EtcdDiscoveryManager", null);
                Dictionary props = config.getProperties();

                if (props == null) {
                    props = new Hashtable();
                }

                props.put("url", "http://localhost:2379/v2/keys");
                System.out.println("props: "+props.toString());

                config.update(props);

                config = confAdmin
                        .getConfiguration("org.inaetics.pubsub.impl.pubsubadmin.zeromq.ZmqPubSubAdmin", null);
                props = config.getProperties();

                if (props == null) {
                    props = new Hashtable();
                }
                System.out.println("props: "+props.toString());

                props.put("sub:zookeeper.connect", "localhost:2181");
                props.put("pub:bootstrap.servers", "localhost:9092");
                config.update(props);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }


    }
}
