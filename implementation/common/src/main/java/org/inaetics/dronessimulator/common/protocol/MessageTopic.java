package org.inaetics.dronessimulator.common.protocol;

import org.inaetics.dronessimulator.pubsub.api.Topic;

/**
 * Different topics.
 */
public enum MessageTopic implements Topic {
    STATEUPDATES ("StateUpdates"),
    MOVEMENTS ("Movements"),
    ARCHITECTURE ("Architecture");

    private String name;

    MessageTopic(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
