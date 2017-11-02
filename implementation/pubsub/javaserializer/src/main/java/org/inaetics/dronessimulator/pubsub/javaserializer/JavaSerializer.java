package org.inaetics.dronessimulator.pubsub.javaserializer;

import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;
import org.inaetics.dronessimulator.pubsub.protocol.Message;

import java.io.*;

/**
 * Serializer implementation using the default Java serialization.
 */
@Log4j
public class JavaSerializer implements Serializer {

    /**
     * Serializes the given message using the Java object serialization.
     * @param message The message to serialize.
     * @return The Java serialized message as byte array.
     * @throws IOException Serialization error in the in-memory output stream.
     */
    public byte[] serialize(Message message) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteStream);

        out.writeObject(message);

        log.debug("Message {} serialized to bytes", message.toString());

        out.close();
        byteStream.close();

        return byteStream.toByteArray();
    }

    /**
     * Deserializes the given byte stream into a message using the Java object deserialization.
     * @param bytes The Java serialized byte array.
     * @return The message built from the byte array.
     * @throws IOException Deserialization error in the in-memory input stream.
     * @throws ClassNotFoundException The given byte array does not deserialize into an instance of a known class.
     */
    public Message deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(byteStream);

        Message message = null;

        try {
            message = (Message) in.readObject();
            log.debug("Deserialized message {} from bytes", message.toString());
        } catch (ClassCastException e) {
            // This is not a valid message so we can drop it.
            log.warn("Invalid message offered for deserialization, message dropped", e);
        }

        in.close();
        byteStream.close();

        return message;
    }
}
