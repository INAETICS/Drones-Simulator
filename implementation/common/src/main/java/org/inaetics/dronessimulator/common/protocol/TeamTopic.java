package org.inaetics.dronessimulator.common.protocol;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.inaetics.dronessimulator.pubsub.api.Topic;

@RequiredArgsConstructor
public class TeamTopic implements Topic {
    @Getter
    private final String name;
}
