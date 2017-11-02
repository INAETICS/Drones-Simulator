package org.inaetics.dronessimulator.pubsub.protocol;

/**
 * Different possible topics of messages for the Subscriber.
 */
public enum MessageTopic implements Topic {
    /**
     * All messages related to drone to drone communication
     */
    RADIO("Radio"),
    /**
     * All messages relating to current state
     */
    STATEUPDATES ("StateUpdates"),
    /**
     * All messages relating to changing the movements of entities
     */
    MOVEMENTS ("Movements"),
    /**
     * All messages relating to changing architecture stuff e.g. the current state
     */
    ARCHITECTURE ("Architecture");

    private String name;

    MessageTopic(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
