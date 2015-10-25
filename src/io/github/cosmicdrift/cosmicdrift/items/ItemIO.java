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
package io.github.cosmicdrift.cosmicdrift.items;

import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.dataio.CompoundAlternativesIO;
import io.github.cosmicdrift.cosmicdrift.dataio.CompoundBaseIO;
import io.github.cosmicdrift.cosmicdrift.dataio.IO;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeReader;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeWriter;

import java.io.IOException;

public class ItemIO implements IO<Item> {

    private final ItemTileEntityIO itemTileEntityIO;

    private final CompoundAlternativesIO<Item> io;

    public ItemIO(World world) {
        this.itemTileEntityIO = new ItemTileEntityIO(world);
        this.io = new CompoundAlternativesIO<>((CompoundBaseIO<? extends Item>) itemTileEntityIO);
    }

    @Override
    public Item load(STreeReader reader) throws IOException {
        if (reader.isAtom()) {
            if (reader.tryNull()) {
                return null;
            }
            String sym = reader.readSymbol();
            switch (sym) {
                case "fluid-monitor":
                    return new ItemFluidsMonitor();
                case "network-monitor":
                    return new ItemNetworkMonitor();
                case "plating":
                    return new ItemPlating();
                case "wrench":
                    return new ItemWrench();
                default:
                    throw new IOException("No such prefab: " + sym);
            }
        }
        return io.load(reader);
    }

    @Override
    public void save(STreeWriter writer, Item object) throws IOException {
        if (object instanceof ItemFluidsMonitor) {
            writer.writeSymbol("fluid-monitor");
        } else if (object instanceof ItemNetworkMonitor) {
            writer.writeSymbol("network-monitor");
        } else if (object instanceof ItemPlating) {
            writer.writeSymbol("plating");
        } else if (object instanceof ItemWrench) {
            writer.writeSymbol("wrench");
        } else if (object == null) {
            writer.writeNull();
        } else {
            io.save(writer, object);
        }
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof Item;
    }
}
