package uk.co.optimisticpanda.wmrs.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneOffset.UTC;

public class JsonDates {

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static class Serializer extends JsonSerializer<LocalDateTime> {

        @Override
        public void serialize(final LocalDateTime value, final JsonGenerator gen, final SerializerProvider serializers)
                throws IOException {

            String dateValue = value.format(DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT));
            String text = "{ \"$date\" : \"" + dateValue + "\"}";
            gen.writeRawValue(text);
        }
    }

    public static class Deserializer extends JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(final JsonParser parser, final DeserializationContext ctxt) throws IOException{
            ObjectCodec oc = parser.getCodec();
            JsonNode node = oc.readTree(parser);
            long dateValue = node.get("$date").longValue();
            return Instant.ofEpochMilli(dateValue).atZone(UTC).toLocalDateTime();
        }
    }
}
