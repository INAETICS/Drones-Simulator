package org.inaetics.dronessimulator.common.protocol;

import org.inaetics.dronessimulator.pubsub.api.Topic;

/**
 * Different topics.
 */
public enum MessageTopic implements Topic {
    RADIO("RADIO"),
    STATEUPDATES ("StateUpdates"),
    MOVEMENTS ("Movements");


    private String name;

    MessageTopic(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
