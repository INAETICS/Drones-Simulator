package org.inaetics.dronessimulator.pubsub.rabbitmq;

import org.inaetics.dronessimulator.pubsub.api.Topic;

/**
 * A topic used for tests.
 */
public class TestTopic implements Topic {
    private static final String NAME = "testTopic";

    @Override
    public String getName() {
        return NAME;
    }
}
