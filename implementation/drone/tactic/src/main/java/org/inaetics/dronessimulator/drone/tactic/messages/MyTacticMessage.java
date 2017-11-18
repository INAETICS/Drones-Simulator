package org.inaetics.dronessimulator.drone.tactic.messages;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.protocol.TacticMessage;
import org.inaetics.dronessimulator.drone.tactic.Tactic;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Log4j
public abstract class MyTacticMessage {
    private final Tactic tactic;

    public TacticMessage getMessage() {
        //Check if the type is registered
        if (!MESSAGETYPES.getMessageTypes().contains(getType())) {
            throw new InvalidMessageTypeException(getType());
        }

        TacticMessage message = new TacticMessage();
        message.put("id", tactic.getIdentifier());
        message.put("type", getType());
        message.putAll(getData());
        return message;
    }

    /**
     * This method is called to add all the data to the final message. This should return a map of the data. This
     * method can also be used to modify the data of a message from outside the subclass.
     *
     * @return a map with the data
     */
    protected abstract Map<String, String> getData();

    /**
     * This method is intended to be overridden if a subclass is of a different type of message than its class name
     *
     * @return the type of the message
     */
    protected String getType() {
        return getClass().getSimpleName();
    }

    public static <M extends MyTacticMessage> boolean checkType(TacticMessage newMessage, Class<M> messageClass) {
        return messageClass.getSimpleName().equals(newMessage.get("type"));
    }

    public static <M extends MyTacticMessage> boolean checkType(TacticMessage newMessage, String messageType) {
        return messageType != null && messageType.equals(newMessage.get("type"));
    }

    public static class MESSAGETYPES {
        public static final String HeartbeatMessage = HeartbeatMessage.class.getSimpleName();
        public static final String RadarImageMessage = RadarImageMessage.class.getSimpleName();
        public static final String FiredBulletMessage = "FiredBulletMessage";
        public static final String SearchLeaderMessage = "SearchLeaderMessage";
        public static final String IsLeaderMessage = "IsLeaderMessage";

        public static List<String> getMessageTypes() {
            return Arrays.asList(Arrays.stream(MESSAGETYPES.class.getFields()).map(field -> {
                try {
                    return field.get(null);
                } catch (IllegalAccessException e) {
                    log.error(e);
                    return null;
                }
            }).toArray(String[]::new));
        }
    }

    @AllArgsConstructor
    @ToString
    public class InvalidMessageTypeException extends RuntimeException {
        private String invalidMessageType;
    }
}
