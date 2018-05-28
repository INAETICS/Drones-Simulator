package org.inaetics.dronessimulator.common.protocol;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.inaetics.dronessimulator.pubsub.api.Message;

import java.util.List;

/**
 * Abstract class for Drone Simulator messages.
 */
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonTypeInfo(
//        use = JsonTypeInfo.Id.NAME,
//        include = JsonTypeInfo.As.WRAPPER_ARRAY
//)
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = CollisionMessage.class, name = "CollisionMessage"),
//        @JsonSubTypes.Type(value = CreateEntityMessage.class, name = "CreateEntityMessage"),
//        @JsonSubTypes.Type(value = DamageMessage.class, name = "DamageMessage"),
//        @JsonSubTypes.Type(value = FireBulletMessage.class, name = "FireBulletMessage"),
//        @JsonSubTypes.Type(value = GameFinishedMessage.class, name = "GameFinishedMessage"),
//        @JsonSubTypes.Type(value = KillMessage.class, name = "KillMessage"),
//        @JsonSubTypes.Type(value = MovementMessage.class, name = "MovementMessage"),
//        @JsonSubTypes.Type(value = RequestArchitectureStateChangeMessage.class, name = "RequestArchitectureStateChangeMessage"),
//        @JsonSubTypes.Type(value = StateMessage.class, name = "StateMessage"),
//        @JsonSubTypes.Type(value = TacticMessage.class, name = "TacticMessage"),
//        @JsonSubTypes.Type(value = TargetMoveLocationMessage.class, name = "TargetMoveLocationMessage"),
//        @JsonSubTypes.Type(value = TextMessage.class, name = "TextMessage"),
//})
public abstract class ProtocolMessage implements Message {
    /**
     * First create an instance, then use setters.
     */
    public ProtocolMessage() {}

    public abstract List<MessageTopic> getTopics();
}

