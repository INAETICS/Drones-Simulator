package org.inaetics.dronessimulator.pubsub.impl.serializer.java;

import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test case for the Java serializer implementation.
 */
public class JavaSerializerTest {
    @Test
    public void serializeAndDeserialize() throws Exception {
        // Construct serializer instance
        Serializer serializer = new JavaSerializer();

        // Construct message
        String message = "This message is a test.";
        TestMessage input = new TestMessage(message);

        // Serialize and deserialize the message
        byte[] serialized = serializer.serialize(input);
        TestMessage output = (TestMessage) serializer.deserialize(serialized);

        // Make sure the message itself is restored
        assertEquals("The input message is different from the output message", message, output.getMessage());

        // Make sure the non-serializable is empty
        assertNull("The non-serializable field was serialized", output.getNotSerialized());
    }
}