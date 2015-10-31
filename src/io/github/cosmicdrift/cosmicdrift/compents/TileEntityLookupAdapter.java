package io.github.cosmicdrift.cosmicdrift.compents;

import com.google.gson.*;

import java.lang.reflect.Type;

public class TileEntityLookupAdapter implements JsonSerializer<TileEntityType>, JsonDeserializer<TileEntityType> {
    @Override
    public TileEntityType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return TileEntityType.forName(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(TileEntityType tileEntityType, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(tileEntityType.typename);
    }
}
