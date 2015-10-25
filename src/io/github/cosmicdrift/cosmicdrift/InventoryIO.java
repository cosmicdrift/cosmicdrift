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

import io.github.cosmicdrift.cosmicdrift.dataio.CompressedListIO;
import io.github.cosmicdrift.cosmicdrift.dataio.IO;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeReader;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeWriter;
import io.github.cosmicdrift.cosmicdrift.items.Item;
import io.github.cosmicdrift.cosmicdrift.items.ItemIO;

import java.io.IOException;
import java.util.List;

public class InventoryIO implements IO<Inventory> {

    private final ItemIO itemIO;
    private final IO<List<Item>> invIO;

    public InventoryIO(World world) {
        this.itemIO = new ItemIO(world);
        this.invIO = new CompressedListIO<>(null, itemIO, Item.class);
    }

    @Override
    public Inventory load(STreeReader reader) throws IOException {
        reader.beginList();
        Inventory inv = new Inventory(reader.readInteger());
        inv.leftHand = itemIO.load(reader);
        inv.rightHand = itemIO.load(reader);
        inv.addAllFromLoad(invIO.load(reader));
        reader.endList();
        return inv;
    }

    @Override
    public void save(STreeWriter writer, Inventory object) throws IOException {
        writer.beginList();
        writer.writeInteger(object.slotCount);
        itemIO.save(writer, object.leftHand);
        itemIO.save(writer, object.rightHand);
        invIO.save(writer, object.inv);
        writer.endList();
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof Inventory;
    }
}
