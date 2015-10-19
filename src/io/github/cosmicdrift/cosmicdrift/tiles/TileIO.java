package io.github.cosmicdrift.cosmicdrift.tiles;

import io.github.cosmicdrift.cosmicdrift.dataio.IO;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeReader;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeWriter;

import java.io.IOException;

public class TileIO implements IO<Tile> {

    @Override
    public Tile load(STreeReader reader) throws IOException {
        if (reader.tryNull()) {
            return null;
        } else {
            reader.beginList();
            int o2 = reader.readInteger();
            int co2 = reader.readInteger();
            int n2 = reader.readInteger();
            reader.endList();
            return new Tile(o2, co2, n2);
        }
    }

    @Override
    public void save(STreeWriter writer, Tile object) throws IOException {
        if (object == null) {
            writer.writeNull();
        } else {
            writer.beginList();
            writer.writeInteger(object.o2);
            writer.writeInteger(object.co2);
            writer.writeInteger(object.n2);
            writer.endList();
        }
    }

    @Override
    public boolean accepts(Object o) {
        return o == null || o instanceof Tile;
    }
}
