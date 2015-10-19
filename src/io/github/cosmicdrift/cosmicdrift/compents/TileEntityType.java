package io.github.cosmicdrift.cosmicdrift.compents;

import com.google.gson.Gson;
import io.github.cosmicdrift.cosmicdrift.items.ItemTileEntity;
import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.components.Component;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeReader;
import io.github.cosmicdrift.cosmicdrift.items.Item;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TileEntityType {

    private static Collection<TileEntityType> loadAll() throws IOException {
        ArrayList<TileEntityType> types = new ArrayList<>();
        InputStream i = TileEntityType.class.getResourceAsStream("presets.sxp");
        if (i == null) {
            throw new IOException("Could not load TileEntity presets!");
        }
        Gson gson = new Gson();
        try (STreeReader reader = new STreeReader(new InputStreamReader(i))) {
            TileEntityTypeIO loader = new TileEntityTypeIO();
            while (!reader.isEOF()) {
                TileEntityType loaded = loader.load(reader);
                System.out.println("Conversion of " + loaded.typename + " to JSON:");
                System.out.println(gson.toJson(loaded));
                System.out.println("==============================================");
                types.add(loaded);
            }
        }
        return types;
    }

    private static HashMap<String, TileEntityType> lookup = new HashMap<>();

    static {
        try {
            for (TileEntityType tile : loadAll()) {
                lookup.put(tile.typename, tile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static TileEntityType forName(String name) {
        TileEntityType tile = lookup.get(name);
        if (tile == null) {
            throw new IllegalArgumentException("Invalid tile entity: " + name);
        }
        return tile;
    }

    public final String typename;
    public final Component[] components;
    public final HashMap<String, Object> defaults;

    public TileEntityType(String typename, Component[] components, HashMap<String, Object> defaults) {
        this.typename = typename;
        this.components = components;
        this.defaults = defaults;
    }

    public Item getAsItemForDrop() {
        if (!defaults.containsKey("item-icon")) {
            throw new RuntimeException("Cannot directly get droppable item without an item-icon setting!");
        }
        return new ItemTileEntity(this, null, defaults);
    }
}
