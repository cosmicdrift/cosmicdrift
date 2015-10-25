/*
    Cosmic Drift is a computer game about building simulated space stations.
    Copyright (C) 2014-2015 Colby Skeggs.

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
package io.github.cosmicdrift.cosmicdrift.entities;

import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.dataio.CompoundBaseIO;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeReader;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeWriter;
import io.github.cosmicdrift.cosmicdrift.items.ItemIO;

import java.io.IOException;

public class EntityItemIO extends CompoundBaseIO<EntityItem> {

    private final World world;
    private final ItemIO itemIO;

    public EntityItemIO(World world) {
        super("item");
        this.world = world;
        this.itemIO = new ItemIO(world);
    }
    
    @Override
    protected EntityItem loadEnd(STreeReader reader) throws IOException {
        EntityItem out = new EntityItem(reader.readInteger(), reader.readInteger(), itemIO.load(reader), world);
        reader.endList();
        return out;
    }

    @Override
    protected void saveMiddle(STreeWriter writer, EntityItem data) throws IOException {
        writer.writeInteger(data.x1);
        writer.writeInteger(data.y1);
        itemIO.save(writer, data.item);
    }

    @Override
    public boolean accepts(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
