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

public class CompoundArrayIO<Elem> extends CompoundBaseIO<Elem[]> {

    private final IO<Elem> io;
    private final Class<Elem> cls;

    public CompoundArrayIO(String name, IO<Elem> io, Class<Elem> cls) {
        super(name);
        this.io = io;
        this.cls = cls;
    }

    @Override
    protected Elem[] loadEnd(STreeReader reader) throws IOException {
        ArrayList<Elem> b = new ArrayList<>();
        while (!reader.tryEndList()) {
            b.add(io.load(reader));
        }
        return b.toArray((Elem[]) Array.newInstance(cls, b.size()));
    }

    @Override
    protected void saveMiddle(STreeWriter writer, Elem[] data) throws IOException {
        for (Elem e : data) {
            io.save(writer, e);
        }
    }

    @Override
    public boolean accepts(Object o) {
        if (!(o instanceof Object[]) || o.getClass().getComponentType() != cls) {
            return false;
        }
        for (Object e : (Object[]) o) {
            if (!io.accepts(e)) {
                return false;
            }
        }
        return true;
    }
}
