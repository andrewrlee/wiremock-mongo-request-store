package uk.co.optimisticpanda.wmrs.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

class JacksonCodecProvider implements CodecProvider {
    private final ObjectMapper objectMapper;

    JacksonCodecProvider(final ObjectMapper bsonObjectMapper) {
        this.objectMapper = bsonObjectMapper;
    }

    @Override
    public <T> Codec<T> get(final Class<T> type, final CodecRegistry registry) {

            return new JacksonCodec<>(objectMapper, registry, type);

    }
}