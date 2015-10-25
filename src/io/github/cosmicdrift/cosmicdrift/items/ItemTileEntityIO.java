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
import io.github.cosmicdrift.cosmicdrift.compents.TileEntityType;
import io.github.cosmicdrift.cosmicdrift.dataio.Compound2;
import io.github.cosmicdrift.cosmicdrift.dataio.Compound2IO;
import io.github.cosmicdrift.cosmicdrift.dataio.CompoundBaseIO;
import io.github.cosmicdrift.cosmicdrift.dataio.CompoundListIO;
import io.github.cosmicdrift.cosmicdrift.dataio.ExpressionIO;
import io.github.cosmicdrift.cosmicdrift.dataio.IO;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeReader;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeWriter;
import io.github.cosmicdrift.cosmicdrift.dataio.SymbolIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemTileEntityIO extends CompoundBaseIO<ItemTileEntity> {

    private final IO<List<Compound2<String, Object>>> varIO = new CompoundListIO<>(null,
            new Compound2IO<>(null, new SymbolIO(), new ExpressionIO()));
    
    private final World world;

    public ItemTileEntityIO(World world) {
        super("tile-entity");
        this.world = world;
    }

    @Override
    protected ItemTileEntity loadEnd(STreeReader reader) throws IOException {
        String icon = reader.readString();
        TileEntityType type = TileEntityType.forName(reader.readSymbol());
        HashMap<String, Object> vars = new HashMap<>();
        for (Compound2<String, Object> var : varIO.load(reader)) {
            vars.put(var.a, var.b);
        }
        reader.endList();
        return new ItemTileEntity(type, icon, vars);
    }

    @Override
    protected void saveMiddle(STreeWriter writer, ItemTileEntity data) throws IOException {
        writer.writeString(data.getIcon());
        writer.writeSymbol(data.type.typename);
        ArrayList<Compound2<String, Object>> out = new ArrayList<>();
        for (Map.Entry<String, Object> entry : data.getVars().entrySet()) {
            out.add(new Compound2<>(entry.getKey(), entry.getValue()));
        }
        varIO.save(writer, out);
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof ItemTileEntity;
    }
}
