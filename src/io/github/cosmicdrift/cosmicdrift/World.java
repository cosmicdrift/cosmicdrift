/*
    Cosmic Drift is a computer game about building simulated space stations.
    Copyright (C) 2014-2015 Cel Skeggs and Christopher Quisling.

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
package io.github.cosmicdrift.cosmicdrift;

import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;
import io.github.cosmicdrift.cosmicdrift.entities.Entity;
import io.github.cosmicdrift.cosmicdrift.entities.EntityItem;
import io.github.cosmicdrift.cosmicdrift.entities.EntityPlayer;
import io.github.cosmicdrift.cosmicdrift.graphics.ConsoleLine;
import io.github.cosmicdrift.cosmicdrift.items.Item;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class World {

    private static final Logger logger = Logger.getLogger(World.class.getName());

    private final LinkedList<ConsoleLine> console = new LinkedList<>();
    private final HashMap<Integer, HashMap<Integer, Chunk>> chunks = new HashMap<>();
    private final ArrayList<Chunk> chunkList = new ArrayList<>();
    public EntityPlayer ply;
    private final SaveLoad saveLoad;

    public World() {
        saveLoad = new SaveLoad(this);
    }

    private void presave() {
        for (Chunk c : chunkList) {
            c.presave(this);
        }
    }

    private void postsave() {
        for (Chunk c : chunkList) {
            c.postsave(this);
        }
    }

    public void load(String filename) throws IOException {
        System.out.println("Loading...");
        addChunksFromLoad(saveLoad.load(filename));
        System.out.println("Postloading...");
        postsave();
        System.out.println("Loaded!");
    }

    public void save(String filename) throws IOException {
        System.out.println("Presaving...");
        this.presave();
        try {
            System.out.println("Saving...");
            saveLoad.save(this.chunkList, filename);
        } finally {
            System.out.println("Postsaving...");
            this.postsave();
        }
        System.out.println("Saved!");
    }

    public Iterable<ConsoleLine> getConsoleLines() {
        return console;
    }

    public Iterable<Chunk> getChunkList() {
        return chunkList;
    }

    void addChunksFromLoad(List<Chunk> adding) throws IOException {
        for (Chunk c : adding) {
            HashMap<Integer, Chunk> map = chunks.get(c.cx);
            if (map == null) {
                map = new HashMap<>();
                chunks.put(c.cx, map);
            }
            Chunk rep = map.get(c.cy);
            if (rep != null) {
                throw new IOException("Duplicate chunk for " + c.cx + ", " + c.cy);
            }
            map.put(c.cy, c);
            chunkList.add(c);
            for (Entity e : c.entities) {
                if (e instanceof EntityPlayer) {
                    if (ply != null) {
                        throw new IOException("Duplicate player: " + e + " and " + ply + "!");
                    }
                    ply = (EntityPlayer) e;
                }
            }
        }
    }

    public Chunk getChunk(int cx, int cy) {
        HashMap<Integer, Chunk> map = chunks.get(cx);
        return map == null ? null : map.get(cy);
    }

    public Chunk getOrInitChunk(int cx, int cy) {
        HashMap<Integer, Chunk> map = chunks.get(cx);
        if (map == null) {
            map = new HashMap<>();
            chunks.put(cx, map);
        }
        Chunk c = map.get(cy);
        if (c == null) {
            c = new Chunk(cx, cy);
            chunkList.add(c);
            map.put(cy, c);
        }
        return c;
    }

    public Tile getTile(int x, int y) {
        Chunk c = getChunk(x >> 6, y >> 6);
        return c == null ? null : c.getTile(x & 0x3f, y & 0x3f);
    }

    public void putTile(int x, int y, Tile t) {
        Chunk c = getOrInitChunk(x >> 6, y >> 6);
        c.putTile(x & 0x3f, y & 0x3f, t);
        notifyToAdjacent(x, y);
    }

    public void addEntity(Entity e) {
        int x = e.x1 / Tile.TILE_SIZE, y = e.y1 / Tile.TILE_SIZE;
        int cx = x / Chunk.CHUNK_SIZE, cy = y / Chunk.CHUNK_SIZE;
        Chunk chunk = getOrInitChunk(cx, cy);
        chunk.entities.add(e);
    }

    public void dropItemAtPixel(int x, int y, Item i) {
        addEntity(new EntityItem(x, y, i, this));
    }

    public void dropItemAtTile(int tx, int ty, Item i) { // TODO: Align at center of tile instead of sligthly off-center.
        dropItemAtPixel(tx * Tile.TILE_SIZE + Tile.TILE_SIZE / 2, ty * Tile.TILE_SIZE + Tile.TILE_SIZE / 2, i);
    }

    public void tick() {
        if (ply.isDead()) {
            return;
        }
        for (Chunk c : chunkList) {
            c.tick(this);
        }
        while (!console.isEmpty() && console.getFirst().age() > 4000) {
            console.removeFirst();
        }
    }

    public void print(String line) {
        console.addLast(new ConsoleLine(line));
    }

    public Iterable<TileEntity> getTileEntities(int x, int y) {
        Chunk c = getChunk(x >> 6, y >> 6);
        return c == null ? Collections.EMPTY_LIST : c.getTileEntities(x & 0x3f, y & 0x3f);
    }

    public void addTileEntity(TileEntity ent) {
        int x = ent.x, y = ent.y;
        Chunk c = getOrInitChunk(x >> 6, y >> 6);
        c.addTileEntity(x & 0x3f, y & 0x3f, ent);
        ent.onAdded();
        notifyToAdjacent(x, y);
    }

    public void removeTileEntity(int x, int y, TileEntity t) {
        Chunk c = getOrInitChunk(x >> 6, y >> 6);
        c.removeTileEntity(x & 0x3f, y & 0x3f, t);
        t.onRemove();
        notifyToAdjacent(x, y);
    }

    public void notifyToAdjacent(int x, int y) {
        notifyTo(x - 1, y);
        notifyTo(x + 1, y);
        notifyTo(x, y - 1);
        notifyTo(x, y + 1);
        notifyTo(x, y);
    }

    public void notifyTo(int x, int y) {
        for (TileEntity ent : getTileEntities(x, y)) {
            ent.onUpdateNearby();
        }
    }

    public boolean isSolid(int x, int y) {
        for (TileEntity ent : getTileEntities(x, y)) {
            if (ent.isSolid()) {
                return true;
            }
        }
        return false;
    }
}
