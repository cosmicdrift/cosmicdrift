package io.github.cosmicdrift.cosmicdrift;

import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntityIO;
import io.github.cosmicdrift.cosmicdrift.dataio.CompoundBaseIO;
import io.github.cosmicdrift.cosmicdrift.dataio.CompoundListIO;
import io.github.cosmicdrift.cosmicdrift.dataio.CompressedArrayIO;
import io.github.cosmicdrift.cosmicdrift.dataio.IO;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeReader;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeWriter;
import io.github.cosmicdrift.cosmicdrift.entities.Entity;
import io.github.cosmicdrift.cosmicdrift.entities.EntityIO;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;
import io.github.cosmicdrift.cosmicdrift.tiles.TileIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChunkIO extends CompoundBaseIO<Chunk> {

    private final World world;
    private final IO<Tile[]> tilesIO = new CompressedArrayIO<>("tiles", new TileIO(), Tile.class);
    private final EntityIO entityIO;
    private final IO<List<Entity>> entitiesIO;
    private final IO<TileEntity> tileEntityIO;
    private final IO<List<TileEntity>> tileEntitiesIO;

    public ChunkIO(World world) {
        super("chunk");
        this.world = world;
        this.entityIO = new EntityIO(world);
        this.entitiesIO = new CompoundListIO<>("entities", entityIO);
        this.tileEntityIO = new TileEntityIO(world);
        this.tileEntitiesIO = new CompoundListIO<>("cell", tileEntityIO);
    }

    @Override
    protected Chunk loadEnd(STreeReader reader) throws IOException {
        int cx = reader.readInteger();
        int cy = reader.readInteger();
        Tile[] flat = tilesIO.load(reader);
        Tile[][] tiles = new Tile[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE];
        for (int x=0; x<Chunk.CHUNK_SIZE; x++) {
            for (int y=0; y<Chunk.CHUNK_SIZE; y++) {
                tiles[x][y] = Tile.duplicate(flat[x + y * Chunk.CHUNK_SIZE]); // duplication because otherwise they're all the same object.
            }
        }
        List<Entity> ents = entitiesIO.load(reader);
        List<TileEntity> grid = tileEntitiesIO.load(reader);
        reader.endList();
        return new Chunk(cx, cy, tiles, new ArrayList<>(ents), grid);
    }

    @Override
    protected void saveMiddle(STreeWriter writer, Chunk object) throws IOException {
        writer.writeInteger(object.cx);
        writer.writeInteger(object.cy);
        Tile[] out = new Tile[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE];
        for (int x=0; x<Chunk.CHUNK_SIZE; x++) {
            for (int y=0; y<Chunk.CHUNK_SIZE; y++) {
                out[x + y * Chunk.CHUNK_SIZE] = object.contents[x][y];
            }
        }
        tilesIO.save(writer, out);
        entitiesIO.save(writer, object.entities);
        tileEntitiesIO.save(writer, object.tileEntityList);
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof Chunk;
    }
}
