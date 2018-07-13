package inaetics;

import org.apache.felix.dm.annotation.api.Destroy;
import org.apache.felix.dm.annotation.api.Init;
import org.apache.felix.dm.annotation.api.Start;
import org.apache.felix.dm.annotation.api.Stop;
import inaetics.subscriber.DemoSubscriber;
import org.apache.felix.dm.tracker.ServiceTracker;
import org.inaetics.pubsub.api.pubsub.Publisher;
import org.inaetics.pubsub.api.pubsub.Subscriber;
import org.osgi.framework.*;

public class DemoPublisher {
    private volatile Publisher publisher;
    private BundleContext bundleContext = FrameworkUtil.getBundle(DemoPublisher.class).getBundleContext();
    private volatile ServiceTracker tracker;
    private volatile PublishThread publishThread;
    private Subscriber subscriber;
    private Thread thread;
    private String topic;

    @Init
    protected final void init() {
        System.out.println("INITIALIZED " + this.getClass().getName());
        this.topic = "test";
    }

    @Start
    protected final void start() throws Exception {
        System.out.println("STARTED " + this.getClass().getName());

        subscriber = new DemoSubscriber();

//        if (publishThread != null) { //TODO: Not needed
//            publishThread.interrupt();
//            tracker.close();
//        }
//        try {
           /* String filterString = "(&(objectClass=" + Publisher.class.getName() + ")"
                                + "(" + Publisher.PUBSUB_TOPIC + "=" + topic + "))";
                    ;
            Filter filter = bundleContext.createFilter(filterString);
            tracker = new ServiceTracker(bundleContext, filter, null);
            tracker.open();
            System.out.println("Opened tracker for: "+filterString);
            publisher = (Publisher) tracker.waitForService(0);*/
            System.out.println("Found publisher, starting publisher thread.");
            publishThread = new PublishThread();
            publishThread.start();
//        } catch (InvalidSyntaxException | InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    @Stop
    protected final void stop() throws Exception {
        System.out.print("joining thread... " );
        //TODO: actually stop thread
//        thread.interrupt();
        publishThread.running = false;
        thread.join();
        publishThread = null;
//        tracker.close();
        System.out.println("STOPPED "+ this.getClass().getName());
        publisher = null;
    }

    @Destroy
    void destroy() {
        System.out.println("DESTROYED " + this.getClass().getName());
    }

    private class PublishThread extends Thread {
        PublishThread(){}

        public boolean running = true;

        @Override
        public void run() {
            int i = 0;
            while(running) {
                System.out.println(running);
                if (publisher != null) {
                    String msg = "Msg " + i++;
                    System.out.println("Sending message: " + msg);
                   publisher.send(msg);
                } else {
                    System.out.println("Trying to send on an unitialized publisher!");
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}
