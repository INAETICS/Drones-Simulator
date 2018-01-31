package org.inaetics.dronessimulator.common.protocol;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.inaetics.dronessimulator.pubsub.api.Topic;

@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
public class TeamTopic implements Topic {
    @Getter
    private final String name;
}
