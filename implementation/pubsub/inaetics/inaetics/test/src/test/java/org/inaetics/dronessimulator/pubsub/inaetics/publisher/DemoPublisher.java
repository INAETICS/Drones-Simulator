package org.inaetics.dronessimulator.pubsub.inaetics.publisher;

import org.apache.felix.dm.annotation.api.Destroy;
import org.apache.felix.dm.annotation.api.Init;
import org.apache.felix.dm.annotation.api.Start;
import org.apache.felix.dm.annotation.api.Stop;
import org.apache.felix.dm.tracker.ServiceTracker;
import org.inaetics.dronessimulator.pubsub.inaetics.SendDemoObj;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

public class DemoPublisher {
    private BundleContext bundleContext = FrameworkUtil.getBundle(DemoPublisher.class).getBundleContext();
    private volatile ServiceTracker tracker;
    private volatile org.inaetics.pubsub.api.pubsub.Publisher publisher;
    private volatile PublishThread publishThread;
    private String topic;
    private SendDemoObj content;
    private final String CONTENT_BASE = "msg";

    private static boolean firstTime = true;

    @Init
    protected final void init(){
        System.out.println("INITIALIZED " + this.getClass().getName());
        this.topic = "test";
        this.content = new SendDemoObj(CONTENT_BASE);
    }

    @Start
    protected final void start(){
        System.out.println("STARTED " + this.getClass().getName());

        if (publishThread != null) {
            publishThread.interrupt();
            tracker.close();
        }
        try {
            Filter filter = bundleContext.createFilter("(&(objectClass=" + org.inaetics.pubsub.api.pubsub.Publisher.class.getName() + ")"
                    + "(" + org.inaetics.pubsub.api.pubsub.Publisher.PUBSUB_TOPIC + "=" + topic + "))");
            tracker = new ServiceTracker(bundleContext, filter, null);
            tracker.open();
            publisher = (org.inaetics.pubsub.api.pubsub.Publisher) tracker.waitForService(0);

            publishThread = new PublishThread();
            publishThread.start();
        } catch (InvalidSyntaxException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Stop
    protected final void stop(){
        System.out.println("STOPPED " + this.getClass().getName());

        publishThread.interrupt();
        publishThread = null;
        tracker.close();
        publisher = null;
    }

    @Destroy
    protected final void destroy(){
        System.out.println("DESTROYED " + this.getClass().getName());
    }

    private class PublishThread extends Thread {

        PublishThread(){
//            location.setDescription("fw-" + bundleContext.getProperty(FelixConstants.FRAMEWORK_UUID)  + " [TID=" + this.getId() + "]");
        }

        @Override
        public void run() {
            int msgCount = 0;
            while (!this.isInterrupted()) {

                if (publisher != null) {
                    content.setContent(CONTENT_BASE + msgCount++);
                    publisher.send(content);
                    System.out.printf("Sent %s", content);
                }
                try {
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}
