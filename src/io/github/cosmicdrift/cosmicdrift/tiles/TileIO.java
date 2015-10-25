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
