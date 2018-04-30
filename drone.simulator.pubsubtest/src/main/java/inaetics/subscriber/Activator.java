package inaetics.subscriber;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.pubsub.api.pubsub.Subscriber;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

import java.util.Properties;

import static org.inaetics.pubsub.api.pubsub.Subscriber.PUBSUB_TOPIC;

public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dm) throws Exception {

        Properties properties = new Properties();
        String[] interfaces = new String[]{Subscriber.class.getName()};
        properties.setProperty(PUBSUB_TOPIC,
                "test");
        dm.add(createComponent()
                .setImplementation(DemoSubscriber.class)
                .setInterface(interfaces, properties)
                .add(dm.createServiceDependency()
                        .setService(LogService.class))

                .setCallbacks("init", "connect", "disconnect", "destroy")
        );
    }
}
