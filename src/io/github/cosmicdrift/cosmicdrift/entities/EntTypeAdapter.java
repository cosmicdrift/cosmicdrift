package io.github.cosmicdrift.cosmicdrift.entities;

import com.google.gson.*;
import io.github.cosmicdrift.cosmicdrift.components.Component;
import io.github.cosmicdrift.cosmicdrift.components.CompRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

public class EntTypeAdapter implements JsonSerializer<Entity>, JsonDeserializer<Entity> {

    @Override
    public Entity deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        Class<? extends Entity> entClass = EntRegistry.forName(obj.getAsJsonPrimitive("entity_type").getAsString());
        obj.remove("entity_type");
        return jsonDeserializationContext.deserialize(obj, entClass);
    }

    @Override
    public JsonElement serialize(Entity component, Type type, JsonSerializationContext jsonSerializationContext) {
        String name = EntRegistry.forClass(component.getClass());
        JsonObject obj = jsonSerializationContext.serialize(component, component.getClass()).getAsJsonObject();
        if (obj.get("entity_type") != null) {
            throw new RuntimeException("Serializer for entity type " + name + " must have added an entity_type!");
        }
        obj.addProperty("entity_type", name);
        return obj;
    }
}
