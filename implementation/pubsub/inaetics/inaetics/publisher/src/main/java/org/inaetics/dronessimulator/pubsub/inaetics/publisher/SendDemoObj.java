package org.inaetics.dronessimulator.pubsub.inaetics.publisher;

public class SendDemoObj {
    private String content;

    public SendDemoObj() {
    }

    public SendDemoObj(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "SendDemoObj{" +
                "content='" + content + '\'' +
                '}';
    }
}
