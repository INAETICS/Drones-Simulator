package org.inaetics.dronessimulator.common.protocol.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;

public class LocaltimeSerializer extends StdSerializer<LocalTime>{

    public LocaltimeSerializer() {
        this(null);
    }

    public LocaltimeSerializer(Class<LocalTime> t) {
        super(t);
    }

    @Override
    public serialize(LocalTime value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonProcessingException {
            jgen.writeStartObject();
            jgen.writeNumberField("Hour", value.getHour());
            jgen.writeNumberField("minute",value.getMinute());
            jgen.writeNumberField("nano",value.getNano());
            jgen.writeNumberField("second",value.getSecond());
            jgen.writeEndObject();
    }
    }
