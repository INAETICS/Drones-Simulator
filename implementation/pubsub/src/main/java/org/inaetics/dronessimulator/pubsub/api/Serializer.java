package org.inaetics.dronessimulator.pubsub.api;

import java.io.IOException;

/**
 * Interface for a message serializer.
 */
public interface Serializer {
    /**
     * Serializes the given message to a byte array.
     * @param message The message to serialize.
     * @return The serialized message as byte array.
     * @throws IOException The message could not be serialized.
     */
    byte[] serialize(Message message) throws IOException;

    /**
     * Deserializes the given byte array to a message.
     * @param bytes The serialized byte array.
     * @return The reconstructed message.
     * @throws NoClassDefFoundError The class of the serialized object could not be found.
     */
    Message deserialize(byte[] bytes) throws NoClassDefFoundError;
}
