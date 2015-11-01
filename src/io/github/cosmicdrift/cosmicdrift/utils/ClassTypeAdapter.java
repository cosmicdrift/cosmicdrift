package io.github.cosmicdrift.cosmicdrift.utils;

import com.google.gson.*;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class ClassTypeAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    public static final String JSON_FIELD = "class_type";
    private final ClassRegistry<T> registry;

    public ClassTypeAdapter(ClassRegistry<T> registry) {
        this.registry = registry;
    }

    @Override
    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        Class<? extends T> entClass = registry.forName(obj.getAsJsonPrimitive(JSON_FIELD).getAsString());
        obj.remove(JSON_FIELD);
        return jsonDeserializationContext.deserialize(obj, entClass);
    }

    @Override
    public JsonElement serialize(T component, Type type, JsonSerializationContext jsonSerializationContext) {
        String name = registry.forClass(Utils.getGenericClass(component));
        JsonObject obj = jsonSerializationContext.serialize(component, component.getClass()).getAsJsonObject();
        if (obj.get(JSON_FIELD) != null) {
            throw new RuntimeException("Serializer for entity type " + name + " must have added a class_type!");
        }
        obj.addProperty(JSON_FIELD, name);
        return obj;
    }
}
