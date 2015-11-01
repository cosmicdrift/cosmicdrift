package io.github.cosmicdrift.cosmicdrift;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntityLookupAdapter;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntityType;
import io.github.cosmicdrift.cosmicdrift.components.Component;
import io.github.cosmicdrift.cosmicdrift.entities.Entity;
import io.github.cosmicdrift.cosmicdrift.items.Item;
import io.github.cosmicdrift.cosmicdrift.utils.ClassTypeAdapter;
import io.github.cosmicdrift.cosmicdrift.utils.Registries;
import io.github.cosmicdrift.cosmicdrift.utils.SingletonTypeAdapter;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SaveLoad {
    private final Gson gson;

    public SaveLoad(World world) {
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(TileEntityType.class, new TileEntityLookupAdapter());
        gb.registerTypeAdapter(Chunk.class, new ChunkAdapter());
        gb.registerTypeHierarchyAdapter(Component.class, new ErrorAdapter<Component>("Cannot save or load components!"));
        gb.registerTypeAdapter(Entity.class, new ClassTypeAdapter<>(Registries.entityRegistry));
        gb.registerTypeAdapter(Item.class, new ClassTypeAdapter<>(Registries.itemRegistry));
        gb.registerTypeAdapter(World.class, new SingletonTypeAdapter<>(world));
        gson = gb.create();
    }

    public void save(List<Chunk> chunkList, String filename) throws IOException {
        try (Writer writer = new OutputStreamWriter(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(filename))))) {
            gson.toJson(chunkList, writer);
        }
    }

    public List<Chunk> load(String filename) throws IOException {
        try (Reader reader = new InputStreamReader(new BufferedInputStream(new GZIPInputStream(new FileInputStream(filename))))) {
            return gson.fromJson(reader, new TypeToken<List<Chunk>>() {
            }.getType());
        }
    }
}
