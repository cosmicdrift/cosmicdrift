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
import io.github.cosmicdrift.cosmicdrift.components.Component;
import io.github.cosmicdrift.cosmicdrift.dataio.ComponentIO;
import io.github.cosmicdrift.cosmicdrift.dataio.Compound2;
import io.github.cosmicdrift.cosmicdrift.dataio.Compound2IO;
import io.github.cosmicdrift.cosmicdrift.dataio.CompoundAlternativesIO;
import io.github.cosmicdrift.cosmicdrift.dataio.CompoundHeadedList;
import io.github.cosmicdrift.cosmicdrift.dataio.CompoundHeadedListIO;
import io.github.cosmicdrift.cosmicdrift.dataio.ExpressionIO;
import io.github.cosmicdrift.cosmicdrift.dataio.IO;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeReader;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeWriter;
import io.github.cosmicdrift.cosmicdrift.dataio.SymbolIO;
import io.github.cosmicdrift.cosmicdrift.dataio.WrappedCompoundIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TileEntityTypeIO implements IO<TileEntityType> {

    private final IO<Entry> lineLoader = new CompoundAlternativesIO<>( // TODO: This can probably be cleaned up just a bit.
            new WrappedCompoundIO<Compound2<String, Object>, Entry>(new Compound2IO<>("var",
                            new SymbolIO(),
                            new ExpressionIO())) {
                @Override
                protected Entry convert(final Compound2<String, Object> var) {
                    return new Entry(var);
                }

                @Override
                protected Compound2<String, Object> deconvert(Entry load) throws IOException {
                    return load.variable;
                }

                @Override
                public boolean accepts(Object o) {
                    return (o instanceof Entry) && !((Entry) o).isComponent;
                }
            },
            new WrappedCompoundIO<Component, Entry>(new ComponentIO("cmp")) {
                @Override
                protected Entry convert(final Component from) {
                    return new Entry(from);
                }

                @Override
                protected Component deconvert(Entry load) throws IOException {
                    return load.component;
                }

                @Override
                public boolean accepts(Object o) {
                    return (o instanceof Entry) && ((Entry) o).isComponent;
                }
            });

    private static class Entry {

        public final boolean isComponent;
        public final Component component;
        public final Compound2<String, Object> variable;

        Entry(Component from) {
            isComponent = true;
            component = from;
            variable = null;
        }

        Entry(Compound2<String, Object> from) {
            isComponent = false;
            component = null;
            variable = from;
        }
    }

    private final CompoundHeadedListIO<String, Entry> preset
            = new CompoundHeadedListIO<>("preset", new SymbolIO(), lineLoader);

    @Override
    public TileEntityType load(STreeReader reader) throws IOException {
        CompoundHeadedList<String, Entry> load = preset.load(reader);

        ArrayList<Component> components = new ArrayList<>();
        HashMap<String, Object> vars = new HashMap<>();

        for (Entry compound : load.body) {
            if (compound.isComponent) {
                components.add(compound.component);
            } else {
                vars.put(compound.variable.a, compound.variable.b);
            }
        }

        return new TileEntityType(load.head, components.toArray(new Component[components.size()]), vars);
    }

    @Override
    public void save(STreeWriter writer, TileEntityType object) throws IOException {
        ArrayList<Entry> entries = new ArrayList<>();
        for (Map.Entry<String, Object> ent : object.defaults.entrySet()) {
            entries.add(new Entry(new Compound2(ent.getKey(), ent.getValue())));
        }
        for (Component comp : object.components) {
            entries.add(new Entry(comp));
        }
        preset.save(writer, new CompoundHeadedList<>(object.typename, entries));
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof TileEntityType;
    }
}
