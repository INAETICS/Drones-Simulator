package org.inaetics.dronessimulator.common.protocol;


import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class KillMessage extends ProtocolMessage {
    /** Indentifier of object */
    private String identifier = null;
    private EntityType entityType;

    @Override
    public List<MessageTopic> getTopics() {
        return Collections.singletonList(MessageTopic.STATEUPDATES);
    }

    @Override
    public String toString() {
        return String.format("(KillMessage %s %s)", this.identifier, this.entityType);
    }
}
