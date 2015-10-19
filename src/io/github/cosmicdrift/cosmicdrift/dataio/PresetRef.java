package io.github.cosmicdrift.cosmicdrift.dataio;

import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntityType;

public class PresetRef {

    private final String name;

    public PresetRef(String name) {
        this.name = name;
    }

    public TileEntityType get(World world) {
        return world.getTileEntityType(name);
    }

    public String getReference() {
        return name;
    }
}
