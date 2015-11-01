package io.github.cosmicdrift.cosmicdrift.utils;

import com.google.gson.*;

import java.lang.reflect.Type;

public class SingletonTypeAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    private final T object;

    public SingletonTypeAdapter(T object) {
        this.object = object;
    }

    @Override
    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.getAsInt() != 0) {
            throw new IllegalArgumentException("There should not be any information associated with a serialized Singleton!");
        }
        return object;
    }

    @Override
    public JsonElement serialize(T t, Type type, JsonSerializationContext jsonSerializationContext) {
        if (t != object) {
            throw new IllegalArgumentException("Attempt to serialize a different instance of a Singleton than expected!");
        }
        return new JsonPrimitive(0);
    }
}
