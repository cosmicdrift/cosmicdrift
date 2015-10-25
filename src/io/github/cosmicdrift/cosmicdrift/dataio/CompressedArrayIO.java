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
package io.github.cosmicdrift.cosmicdrift.dataio;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

// Requires that Elem can be used as a HashMap key.
public class CompressedArrayIO<Elem> extends CompoundBaseIO<Elem[]> {

    private final IO<Elem[]> indexIO;
    private final CompoundArrayIO<Integer> encodeIO;
    private final Class<Elem> etype;

    public CompressedArrayIO(String name, IO<Elem> elemIO, Class<Elem> type) {
        super(name);
        indexIO = new CompoundArrayIO<>("index", elemIO, type);
        encodeIO = new CompoundArrayIO<>("data", new IntegerIO(), Integer.class);
        etype = type;
    }

    @Override
    protected Elem[] loadEnd(STreeReader reader) throws IOException {
        Elem[] index = indexIO.load(reader);
        Integer[] contents = encodeIO.load(reader);
        Elem[] out = (Elem[]) Array.newInstance(etype, contents.length);
        for (int i = 0; i < contents.length; i++) {
            int lkup = contents[i];
            if (lkup < 0 || lkup >= index.length) {
                throw new IOException("Compressed array lookup index out of bounds");
            }
            out[i] = index[lkup];
        }
        reader.endList();
        return out;
    }

    @Override
    protected void saveMiddle(STreeWriter writer, Elem[] data) throws IOException {
        HashMap<Elem, Integer> index = new HashMap<>();
        ArrayList<Elem> lookup = new ArrayList<>();
        Integer[] out = new Integer[data.length];
        for (int i = 0; i < data.length; i++) {
            Elem t = data[i];
            if (!index.containsKey(t)) {
                index.put(t, lookup.size());
                lookup.add(t);
            }
            out[i] = index.get(t);
        }
        indexIO.save(writer, lookup.toArray((Elem[]) Array.newInstance(etype, lookup.size())));
        encodeIO.save(writer, out);
    }

    @Override
    public boolean accepts(Object o) {
        return indexIO.accepts(o);
    }
}
