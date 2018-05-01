package inaetics.subscriber;

import inaetics.SendDemoObj;
import org.inaetics.pubsub.api.pubsub.Subscriber;


public class DemoSubscriber implements Subscriber {
//    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DemoSubscriber.class);


    private String content = "Uninitialized";

    public DemoSubscriber() {
    }

    @Override
    public void receive(Object o, MultipartCallbacks multipartCallbacks) {
        System.out.println("Received object %s" + o.getClass().getName());
        if(o instanceof SendDemoObj) {
            setContent(((SendDemoObj) o).getContent());
        }
    }

    public void init(){
//        System.out.println("DemoSubscriber::init");
    }

    public void destroy(){
        System.out.println("DemoSubscriber::destroy");
    }

    public void connect(){
        System.out.println("DemoSubscriber::connect");
    }

    public void disconnect(){
        System.out.println("DemoSubscriber::disconnect");
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
