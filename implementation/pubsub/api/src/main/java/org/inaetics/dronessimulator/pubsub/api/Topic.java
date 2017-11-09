package org.inaetics.dronessimulator.pubsub.api;

/**
 * Interface for a topic which categorizes messages.
 */
 @FunctionalInterface
public interface Topic {
    /**
     * Returns the name of this topic.
     * @return The name of this topic.
     */
    String getName();
}
