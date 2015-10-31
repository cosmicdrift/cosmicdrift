/*
    Cosmic Drift is a computer game about building simulated space stations.
    Copyright (C) 2014-2015 Colby Skeggs.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.cosmicdrift.cosmicdrift.compents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.cosmicdrift.cosmicdrift.components.CompTypeAdapter;
import io.github.cosmicdrift.cosmicdrift.items.ItemTileEntity;
import io.github.cosmicdrift.cosmicdrift.components.Component;
import io.github.cosmicdrift.cosmicdrift.items.Item;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;

public class TileEntityType {

    private static Collection<TileEntityType> loadAll() throws IOException {
        InputStream i = TileEntityType.class.getResourceAsStream("presets.json");
        if (i == null) {
            throw new IOException("Could not load TileEntity presets!");
        }

        GsonBuilder gb = new GsonBuilder().setPrettyPrinting();
        gb.registerTypeHierarchyAdapter(Component.class, new CompTypeAdapter());
        Gson gson = gb.create();

        Object out = gson.fromJson(new InputStreamReader(i), new TypeToken<Collection<TileEntityType>>() {
        }.getType());
        return (Collection<TileEntityType>) out;
    }

    private static HashMap<String, TileEntityType> lookup = new HashMap<>();

    static {
        try {
            for (TileEntityType tile : loadAll()) {
                if (lookup.containsKey(tile.typename)) {
                    throw new RuntimeException("Duplicate type: " + tile.typename);
                }
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
