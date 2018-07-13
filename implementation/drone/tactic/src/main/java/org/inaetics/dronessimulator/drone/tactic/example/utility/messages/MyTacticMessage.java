package org.inaetics.dronessimulator.drone.tactic.example.utility.messages;

import org.inaetics.dronessimulator.common.protocol.TacticMessage;
import org.inaetics.dronessimulator.drone.tactic.Tactic;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class MyTacticMessage {
    public MyTacticMessage(Tactic tactic) {
        this.tactic = tactic;
    }

    private final Tactic tactic;

    public static <M extends MyTacticMessage> boolean checkType(TacticMessage newMessage, Class<M> messageClass) {
        return messageClass.getSimpleName().equals(newMessage.get("type"));
    }

    public static boolean checkType(TacticMessage newMessage, String messageType) {
        return messageType != null && messageType.equals(newMessage.get("type"));
    }

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
     * Create the logger
     */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MyTacticMessage.class);
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

    public static class MESSAGETYPES {
        public static final String HEARTBEAT_MESSAGE = HeartbeatMessage.class.getSimpleName();
        public static final String RADAR_IMAGE_MESSAGE = RadarImageMessage.class.getSimpleName();
        public static final String INSTRUCTION_MESSAGE = InstructionMessage.class.getSimpleName();
        public static final String FIRED_BULLET_MESSAGE = "FIRED_BULLET_MESSAGE";
        public static final String SEARCH_LEADER_MESSAGE = "SEARCH_LEADER_MESSAGE";
        public static final String IS_LEADER_MESSAGE = "IS_LEADER_MESSAGE";

        private MESSAGETYPES() {
        } //Do not instantiate this static helper (Enum-ish class)

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

    public class InvalidMessageTypeException extends RuntimeException {
        private final String invalidMessageType;

        public InvalidMessageTypeException(String invalidMessageType) {
            this.invalidMessageType = invalidMessageType;
        }

        public InvalidMessageTypeException(String message, String invalidMessageType) {
            super(message);
            this.invalidMessageType = invalidMessageType;
        }

        public InvalidMessageTypeException(String message, Throwable cause, String invalidMessageType) {
            super(message, cause);
            this.invalidMessageType = invalidMessageType;
        }

        public InvalidMessageTypeException(Throwable cause, String invalidMessageType) {
            super(cause);
            this.invalidMessageType = invalidMessageType;
        }

        public InvalidMessageTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String invalidMessageType) {
            super(message, cause, enableSuppression, writableStackTrace);
            this.invalidMessageType = invalidMessageType;
        }

        @Override
        public String toString() {
            return "InvalidMessageTypeException{" +
                    "invalidMessageType='" + invalidMessageType + '\'' +
                    '}';
        }
    }
}
