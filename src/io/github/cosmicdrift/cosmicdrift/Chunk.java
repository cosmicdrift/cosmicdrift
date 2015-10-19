package io.github.cosmicdrift.cosmicdrift;

import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;
import io.github.cosmicdrift.cosmicdrift.entities.Entity;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public final class Chunk {

    public static final int CHUNK_SIZE = 64; // Don't modify this by itself - various shifts and binary-ands are used hardcoded in many places.

    public final int cx, cy;
    public final Tile[][] contents;
    private final HashSet<Integer> needUpdate = new HashSet<>();
    public final ArrayList<Entity> entities;
    final ArrayList<TileEntity> tileEntityList = new ArrayList<>();
    public final List<TileEntity>[][] tileEntities = new List[CHUNK_SIZE][CHUNK_SIZE];

    public Chunk(int cx, int cy) {
        this.cx = cx;
        this.cy = cy;
        this.entities = new ArrayList<>();
        this.contents = new Tile[CHUNK_SIZE][CHUNK_SIZE];
    }

    Chunk(int cx, int cy, Tile[][] contents, ArrayList<Entity> entities, List<TileEntity> tileEntities) {
        this.cx = cx;
        this.cy = cy;
        for (TileEntity ent : tileEntities) {
            addTileEntity(ent.x & 0x3f, ent.y & 0x3f, ent);
        }
        this.entities = entities;
        this.contents = contents;
        if (contents.length != CHUNK_SIZE) {
            throw new RuntimeException("Bad chunk size!");
        }
        for (int x=0; x<CHUNK_SIZE; x++) {
            Tile[] column = contents[x];
            if (column.length != CHUNK_SIZE) {
                throw new RuntimeException("Bad chunk size!");
            }
            for (int y=0; y<CHUNK_SIZE; y++) {
                if (column[y] != null) {
                    needUpdate.add(x | (y << 6));
                }
            }
        }
    }

    public Tile getTile(int x, int y) {
        return contents[x][y];
    }

    public void putTile(int x, int y, Tile t) {
        contents[x][y] = t;
        int off = x | (y << 6);
        if (t != null) {
            needUpdate.add(off);
        } else {
            needUpdate.remove(off);
        }
    }

    public void tick(World world) {
        Integer[] o = needUpdate.toArray(new Integer[needUpdate.size()]);
        for (Integer o1 : o) {
            int v = o1;
            int x = v & 0x3f, y = v >> 6;
            Tile t = getTile(x, y);
            if (t != null) {
                t.tick(world, cx * Chunk.CHUNK_SIZE + x, cy * Chunk.CHUNK_SIZE + y);
            }
        }
        for (TileEntity t : tileEntityList) {
            t.tick();
        }
        for (Iterator<Entity> it = entities.iterator(); it.hasNext();) { // TODO: Automatically move entities between chunks.
            Entity e = it.next();
            if (e.tick()) {
                it.remove();
            }
        }
    }

    public Iterable<TileEntity> getTileEntities(int x, int y) {
        List<TileEntity> o = tileEntities[x][y];
        return o == null ? Collections.EMPTY_LIST : o;
    }

    public void addTileEntity(int x, int y, TileEntity ent) {
        List<TileEntity> o = tileEntities[x][y];
        if (o == null) {
            o = new ArrayList<>();
            tileEntities[x][y] = o;
        }
        o.add(ent);
        tileEntityList.add(ent);
    }

    public void removeTileEntity(int x, int y, TileEntity t) {
        tileEntities[x][y].remove(t);
        tileEntityList.remove(t);
    }
}
