package org.inaetics.dronessimulator.common.protocol;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.inaetics.dronessimulator.pubsub.api.Topic;

@EqualsAndHashCode
@RequiredArgsConstructor
public class TeamTopic implements Topic {
    @Getter
    private final String name;
}
