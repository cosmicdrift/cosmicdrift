package io.github.cosmicdrift.cosmicdrift;

import com.google.gson.*;
import io.github.cosmicdrift.cosmicdrift.components.Component;

import java.lang.reflect.Type;

public class ErrorAdapter<X> implements JsonSerializer<X>, JsonDeserializer<X> {

    private final String error;

    public ErrorAdapter(String error) {
        this.error = error;
    }

    @Override
    public X deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        throw new JsonParseException(error);
    }

    @Override
    public JsonElement serialize(X o, Type type, JsonSerializationContext jsonSerializationContext) {
        throw new JsonParseException(error);
    }
}
