package io.github.cosmicdrift.cosmicdrift.compents;

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

    public static Collection<TileEntityType> loadAll(World world) throws IOException {
        ArrayList<TileEntityType> types = new ArrayList<>();
        InputStream i = TileEntityType.class.getResourceAsStream("presets.sxp");
        if (i == null) {
            throw new IOException("Could not load TileEntity presets!");
        }
        try (STreeReader reader = new STreeReader(new InputStreamReader(i))) {
            TileEntityTypeIO loader = new TileEntityTypeIO(world);
            while (!reader.isEOF()) {
                types.add(loader.load(reader));
            }
        }
        return types;
    }

    public final World world;
    public final String typename;
    public final Component[] components;
    public final HashMap<String, Object> defaults;

    public TileEntityType(World world, String typename, Component[] components, HashMap<String, Object> defaults) {
        this.world = world;
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
