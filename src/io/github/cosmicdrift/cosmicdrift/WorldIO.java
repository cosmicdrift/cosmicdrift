/*
    Cosmic Drift is a computer game about building simulated space stations.
    Copyright (C) 2014-2015 Colby Skeggs and Christopher Quisling.

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

import io.github.cosmicdrift.cosmicdrift.dataio.CompoundListIO;
import io.github.cosmicdrift.cosmicdrift.dataio.IO;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeReader;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeWriter;
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
