package org.inaetics.dronessimulator.pubsub.javaserializer;

import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;

import java.io.*;

/**
 * Serializer implementation using the default Java serialization.
 */
public class JavaSerializer implements Serializer {
    /**
     * Serializes the given message using the Java object serialization.
     *
     * @param message The message to serialize.
     * @return The Java serialized message as byte array.
     * @throws IOException Serialization error in the in-memory output stream.
     */
    public byte[] serialize(Message message) throws IOException {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(); ObjectOutputStream out = new ObjectOutputStream(byteStream)) {
            out.writeObject(message);
            return byteStream.toByteArray();
        }
    }
    /**
     * Create the logger
     */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JavaSerializer.class);


    /**
     * Deserializes the given byte stream into a message using the Java object deserialization.
     *
     * @param bytes The Java serialized byte array.
     * @return The message built from the byte array.
     * @throws IOException            Deserialization error in the in-memory input stream.
     * @throws ClassNotFoundException The given byte array does not deserialize into an instance of a known class.
     */
    public Message deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes); ObjectInputStream in = new ObjectInputStream(byteStream)) {
            return (Message) in.readObject();
        } catch (ClassCastException e) {
            // This is not a valid message so we can drop it.
            log.warn("Invalid message offered for deserialization, message dropped", e);
        }
        return null;
    }
}
