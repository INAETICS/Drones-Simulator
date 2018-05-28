package org.inaetics.dronessimulator.common.protocol.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;

public class LocaltimeDeserializer extends StdDeserializer<LocalTime> {

    public LocaltimeDeserializer() {
        this(null);
    }

    public LocaltimeDeserializer(Class<LocalTime> t) {
        super(t);
    }

    @Override
    public LocalTime deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        int hour = (Integer) ((IntNode) node.get("hour")).numberValue();
        int minute = (Integer) ((IntNode) node.get("minute")).numberValue();
        int second = (Integer) ((IntNode) node.get("second")).numberValue();
        int nano = (Integer) ((IntNode) node.get("nano")).numberValue();

        return LocalTime.of(hour,minute,second,nano);
    }
}
