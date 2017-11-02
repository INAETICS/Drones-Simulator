package org.inaetics.dronessimulator.pubsub.rabbitmq;

import org.inaetics.dronessimulator.pubsub.protocol.Topic;

/**
 * A topic used for tests.
 */
public class TestTopic implements Topic {
    private static final String NAME_APPEND = "Test";

    private String name;

    public TestTopic(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name + NAME_APPEND;
    }
}
