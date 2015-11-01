package io.github.cosmicdrift.cosmicdrift;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;
import io.github.cosmicdrift.cosmicdrift.entities.Entity;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

import java.lang.reflect.Type;
import java.util.*;

public class ChunkAdapter implements JsonSerializer<Chunk>, JsonDeserializer<Chunk> {
    @Override
    public Chunk deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject o = jsonElement.getAsJsonObject();
        int cx = o.getAsJsonPrimitive("cx").getAsInt();
        int cy = o.getAsJsonPrimitive("cy").getAsInt();
        int prefix = o.getAsJsonPrimitive("prefix").getAsInt();

        int[] tiles = jsonDeserializationContext.deserialize(o.get("tiles"), int[].class);

        // convert our compressed integer sequence into a full integer sequence
        int[] fullMap = new int[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE];
        System.arraycopy(tiles, 0, fullMap, prefix, tiles.length);
        int last = tiles[tiles.length - 1];
        for (int i = prefix + tiles.length; i < fullMap.length; i++) {
            fullMap[i] = last;
        }

        // convert our integer sequence into a Tile sequence
        List<Tile> typesO = jsonDeserializationContext.deserialize(o.get("types"), new TypeToken<List<Tile>>() {
        }.getType());
        Tile[] types = typesO.toArray(new Tile[typesO.size()]);
        Tile[][] parsedMap = new Tile[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE];
        for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
            for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
                int num = fullMap[y * Chunk.CHUNK_SIZE + x] - 1;
                if (num != -1) { // don't modify when unused - then it's just null, which was zero in the map (AKA -1 here.)
                    if (num < 0 || num >= types.length) {
                        throw new JsonParseException("Attempt to reference type # that wasn't included!");
                    }
                    parsedMap[x][y] = new Tile(types[num]);
                }
            }
        }

        List<Entity> entities = jsonDeserializationContext.deserialize(o.get("entities"), new TypeToken<List<Entity>>() {
        }.getType());
        List<TileEntity> tileEntitiesList = jsonDeserializationContext.deserialize(o.get("tile_entities"), new TypeToken<Collection<TileEntity>>() {
        }.getType());

        return new Chunk(cx, cy, parsedMap, entities, tileEntitiesList);
    }

    @Override
    public JsonElement serialize(Chunk chunk, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject o = new JsonObject();
        o.addProperty("cx", chunk.cx);
        o.addProperty("cy", chunk.cy);
        HashSet<Tile> tiles = new HashSet<>();
        for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
            for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
                tiles.add(chunk.contents[x][y]);
            }
        }
        tiles.remove(null);
        ArrayList<Tile> used = new ArrayList<>(tiles);
        int[] map = new int[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE];
        for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
            for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
                Tile tile = chunk.contents[x][y];
                if (tile != null) { // don't modify when unused - then it's just zero, which means null.
                    map[y * Chunk.CHUNK_SIZE + x] = used.indexOf(tile) + 1;
                }
            }
        }
        int first = 0;
        // don't bother writing out leading zeros
        while (first < map.length && map[first] == 0) {
            first++;
        }
        int last = map.length - 1;
        // don't bother writing out the final repeating sequence if it occurs
        while (last > first && map[last] == map[last - 1]) {
            last--;
        }
        map = Arrays.copyOfRange(map, first, last + 1);
        o.addProperty("prefix", first);
        o.add("types", jsonSerializationContext.serialize(used, new TypeToken<List<Tile>>() {
        }.getType()));
        o.add("tiles", jsonSerializationContext.serialize(map, int[].class));
        o.add("entities", jsonSerializationContext.serialize(chunk.entities, new TypeToken<Collection<Entity>>() {
        }.getType()));
        o.add("tile_entities", jsonSerializationContext.serialize(chunk.tileEntityList, new TypeToken<Collection<TileEntity>>() {
        }.getType()));
        return o;
    }
}
