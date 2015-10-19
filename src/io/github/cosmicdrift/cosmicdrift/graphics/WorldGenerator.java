package io.github.cosmicdrift.cosmicdrift.graphics;

import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntityType;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

import java.io.IOException;

public class WorldGenerator {

    public static World create() throws IOException {
        WorldGenerator gen = new WorldGenerator();
        gen.generate();
        return gen.world;
    }
    
    public final World world;

    public WorldGenerator() throws IOException {
        this.world = new World();
    }

    private void fill(int x, int y, boolean air) {
        world.putTile(x, y, new Tile(air ? 200 : 0, 0, air ? 800 : 0));
    }
    
    private void entity(int x, int y, String type) {
        world.addTileEntity(new TileEntity(world, TileEntityType.forName(type), x, y));
    }

    private void putWall(int x, int y) {
        fill(x, y, false);
        entity(x, y, "wall");
    }

    private void putDoor(int x, int y) {
        fill(x, y, false);
        entity(x, y, "door");
    }

    public void generate() {
        for (int x = 300; x < 328; x++) {
            if (x >= 314 && x <= 315) {
                continue;
            }
            putWall(x, 300);
            for (int y = 301; y < 313; y++) {
                fill(x, y, true);
            }
            putWall(x, 313);
        }
        entity(305, 305, "lua-computer");
        entity(305, 307, "lua-computer");
        entity(307, 305, "zpm");
        entity(307, 307, "zpm");
        for (int j = 301; j < 313; j++) {
            putWall(300, j);
            if (j < 311 || j > 312) {
                putWall(313, j);
                putWall(316, j);
            }
            putWall(327, j);
        }
        putWall(301, 303);
        putWall(302, 303);
        putWall(303, 303);
        putWall(304, 303);
        putWall(311, 305);
        putWall(311, 306);
        putWall(301, 307);
        putWall(302, 307);
        putWall(303, 307);
        putWall(311, 307);
        putWall(309, 308);
        putWall(310, 308);
        putWall(311, 308);
        putWall(312, 308);
        putWall(301, 310);
        putWall(302, 310);
        putWall(303, 310);
        putWall(304, 310);
        putWall(307, 310);
        entity(307, 310, "medkit");
        putWall(308, 310);
        putWall(309, 310);
        putWall(310, 310);
        putWall(311, 310);
        putWall(312, 310);
        putDoor(313, 311);
        putDoor(313, 312);
        putWall(314, 310);
        fill(314, 311, true);
        fill(314, 312, true);
        putWall(315, 310);
        fill(315, 311, true);
        fill(315, 312, true);
        putWall(316, 310);
        putWall(316, 313);
        putDoor(316, 311);
        putDoor(316, 312);
        putDoor(314, 313);
        putDoor(315, 313);
        entity(310, 311, "zpm");
        entity(315, 311, "conductive-wire");
        entity(311, 311, "conductive-wire");
        entity(321, 307, "gravity");
        for (int y = 308; y <= 311; y++) {
            entity(321, y, "conductive-wire");
        }
        entity(322, 311, "conductive-wire");
        entity(323, 311, "conductive-wire");
        entity(324, 311, "zpm");
        entity(317, 301, "pipe-dispenser");
        entity(318, 301, "vent-dispenser");
        entity(319, 301, "tank-dispenser");
        entity(320, 301, "wire-dispenser");
        entity(321, 301, "data-dispenser");
    }
}
