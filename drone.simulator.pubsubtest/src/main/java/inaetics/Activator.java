package inaetics;

import inaetics.subscriber.DemoSubscriber;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.pubsub.api.pubsub.Publisher;
import org.inaetics.pubsub.api.pubsub.Subscriber;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

//import static org.inaetics.pubsub.api.pubsub.Subscriber.PUBSUB_TOPIC;

public class Activator extends DependencyActivatorBase {

    private final static String TOPIC = "All";

    private BundleContext bundleContext;
    private DependencyManager dm;

    class InternalTest implements Subscriber {
        @Override
        public void receive(Object o, MultipartCallbacks multipartCallbacks) {
            System.out.println("Received object " + o);
        }
    }

    public void doLater() {
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

        InternalTest test = new InternalTest();
        Dictionary<String, String> p = new Hashtable<>();
        p.put(Subscriber.PUBSUB_TOPIC, TOPIC);
        ServiceRegistration registration = bundleContext.registerService(Subscriber.class.getName(), test, p);



    }

    @Override
    public void init(BundleContext bundleContext, DependencyManager dm) throws Exception {
        this.bundleContext = bundleContext;
        this.dm = dm;
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                    System.out.println("done waiting");
                    doLater();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }
}
