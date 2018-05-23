package org.inaetics.dronessimulator.pubsub.inaetics;

import org.apache.felix.dm.annotation.api.Destroy;
import org.apache.felix.dm.annotation.api.Init;
import org.apache.felix.dm.annotation.api.Start;
import org.apache.felix.dm.annotation.api.Stop;
import org.inaetics.dronessimulator.pubsub.inaetics.subscriber.DemoSubscriber;
import org.inaetics.pubsub.api.pubsub.Publisher;
import org.inaetics.pubsub.api.pubsub.Subscriber;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import java.util.Dictionary;
import java.util.Hashtable;

public class Demo {

    public static final String SERVICE_PID = Demo.class.getName();
    private Subscriber subscriber;
    private BundleContext bundleContext = FrameworkUtil.getBundle(Demo.class).getBundleContext();
    private ServiceRegistration registration;

    @Init
    protected final void init(){
        System.out.println("INITIALIZED class " + this.getClass().getName());
        this.subscriber = new DemoSubscriber();
    }

    @Start
    protected final void start(){
        System.out.println("STARTED " + this.getClass().getName());

        Dictionary<String, String> properties = new Hashtable<>();
        properties.put(Subscriber.PUBSUB_TOPIC, "test");
        registration = bundleContext.registerService(Subscriber.class.getName(), subscriber, properties);
    }

    @Stop
    protected final void stop(){
        System.out.println("STOPPED " + this.getClass().getName());
        registration.unregister();
    }

    @Destroy
    protected final void destroy(){
        System.out.println("DESTROYED " + this.getClass().getName());
    }
}
