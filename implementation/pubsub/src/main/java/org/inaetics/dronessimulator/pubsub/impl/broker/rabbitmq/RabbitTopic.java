package org.inaetics.dronessimulator.pubsub.impl.broker.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import org.inaetics.dronessimulator.pubsub.api.broker.Topic;

/**
 * A topic tailored for RabbitMQ publishers and subscribers.
 */
public class RabbitTopic implements Topic {
    /** The default exchange type. FANOUT because that makes sure all queues get all messages. */
    static BuiltinExchangeType DEFAULT_EXCHANGE_TYPE = BuiltinExchangeType.FANOUT;

    /** The name of the topic. To be used as the name of the exchange. */
    private String name;

    /** The exchange type used for this topic. */
    private BuiltinExchangeType exchangeType;

    /** Whether the exchange is persistent. */
    private boolean isPersistent;

    /**
     * Instantiates a new RabbitMQ specific topic.
     * @param name The name of the topic.
     * @param exchangeType The RabbitMQ exchange type to be used.
     * @param isPersistent Whether the RabbitMQ exchange should be persistent.
     */
    public RabbitTopic(String name, BuiltinExchangeType exchangeType, boolean isPersistent) {
        assert name != null;
        assert exchangeType != null;

        this.name = name;
        this.exchangeType = exchangeType;
        this.isPersistent = isPersistent;
    }

    /**
     * Instantiates a new RabbitMQ specific topic with the default exchange settings.
     * @param name The name of the topic.
     */
    public RabbitTopic(String name) {
        this(name, DEFAULT_EXCHANGE_TYPE, false);
    }

    /**
     * Instantiates a new RabbitMQ specific topic from an existing topic.
     * @param topic The topic to copy.
     */
    public RabbitTopic(Topic topic) {
        this(topic.getName());
    }

    /**
     * Returns the name of the topic. This name is used as name for a RabbitMQ exchange.
     * @return The topic name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the exchange type used for this topic.
     * @return The exchange type used for this topic.
     */
    public BuiltinExchangeType getExchangeType() {
        return this.exchangeType;
    }

    /**
     * Returns whether the exchange for this topic is persistent.
     * @return Whether the exchange is persistent.
     */
    public boolean isPersistent() {
        return this.isPersistent;
    }
}
