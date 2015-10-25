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
package io.github.cosmicdrift.cosmicdrift.compents;

import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.dataio.Compound2;
import io.github.cosmicdrift.cosmicdrift.dataio.Compound2IO;
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

public class TileEntityIO implements IO<TileEntity> {

    private final World world;
    private final IO<List<Compound2<String, Object>>> varIO = new CompoundListIO<>(null, new Compound2IO<>(null, new SymbolIO(), new ExpressionIO()));

    public TileEntityIO(World world) {
        this.world = world;
    }

    @Override
    public TileEntity load(STreeReader reader) throws IOException {
        reader.beginList();
        TileEntity out = new TileEntity(world, TileEntityType.forName(reader.readSymbol()), reader.readInteger(), reader.readInteger(), readVars(reader), reader.readString());
        reader.endList();
        return out;
    }

    private Map<String, Object> readVars(STreeReader reader) throws IOException {
        HashMap<String, Object> out = new HashMap<>();
        for (Compound2<String, Object> cmp : varIO.load(reader)) {
            out.put(cmp.a, cmp.b);
        }
        return out;
    }

    @Override
    public void save(STreeWriter writer, TileEntity object) throws IOException {
        writer.beginList();
        writer.writeSymbol(object.type.typename);
        writer.writeInteger(object.x);
        writer.writeInteger(object.y);
        ArrayList<Compound2<String, Object>> vlist = new ArrayList<>();
        for (Map.Entry<String, Object> ent : object.getVarView().entrySet()) {
            vlist.add(new Compound2<>(ent.getKey(), ent.getValue()));
        }
        varIO.save(writer, vlist);
        writer.writeString(object.icon);
        writer.endList();
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof TileEntity;
    }
}
