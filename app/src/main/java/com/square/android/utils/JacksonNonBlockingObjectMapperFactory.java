package com.square.android.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class JacksonNonBlockingObjectMapperFactory {

    /**
     * Deserializer that won't block if value parsing doesn't match with target type
     * @param <T> Handled type
     */
    private static class NonBlockingDeserializer<T> extends JsonDeserializer<T> {
        private StdDeserializer<T> delegate;

        public NonBlockingDeserializer(StdDeserializer<T> _delegate){
            this.delegate = _delegate;
        }

        @Override
        public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            try {
                return delegate.deserialize(jp, ctxt);
            }catch (JsonMappingException e){
                // If a JSON Mapping occurs, simply returning null instead of blocking things
                return null;
            }
        }
    }

    private List<StdDeserializer> jsonDeserializers = new ArrayList<StdDeserializer>();

    public ObjectMapper createObjectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();

        SimpleModule customJacksonModule = new SimpleModule("customJacksonModule", new Version(1, 0, 0, null));
        for(StdDeserializer jsonDeserializer : jsonDeserializers){
            // Wrapping given deserializers with NonBlockingDeserializer
            customJacksonModule.addDeserializer(jsonDeserializer.getValueClass(), new NonBlockingDeserializer(jsonDeserializer));
        }

        objectMapper.registerModule(customJacksonModule);
        return objectMapper;
    }

    public JacksonNonBlockingObjectMapperFactory setJsonDeserializers(List<StdDeserializer> _jsonDeserializers){
        this.jsonDeserializers = _jsonDeserializers;
        return this;
    }
}