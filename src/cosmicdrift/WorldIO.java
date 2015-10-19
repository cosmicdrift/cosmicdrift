package cosmicdrift;

import cosmicdrift.dataio.CompoundListIO;
import cosmicdrift.dataio.IO;
import cosmicdrift.dataio.STreeReader;
import cosmicdrift.dataio.STreeWriter;
import java.io.IOException;
import java.util.List;

public class WorldIO implements IO<World> {

    @Override
    public World load(STreeReader reader) throws IOException {
        World out = new World();
        ChunkIO chio = new ChunkIO(out);
        IO<List<Chunk>> clio = new CompoundListIO<>("world", chio);
        out.addChunksFromLoad(clio.load(reader));
        if (out.ply == null) {
            throw new IOException("No player found!");
        }
        out.print("World Loaded!");
        return out;
    }

    @Override
    public void save(STreeWriter writer, World world) throws IOException {
        ChunkIO chio = new ChunkIO(world);
        IO<List<Chunk>> clio = new CompoundListIO<>("world", chio);
        clio.save(writer, world.getChunkFullList());
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof World;
    }
}
