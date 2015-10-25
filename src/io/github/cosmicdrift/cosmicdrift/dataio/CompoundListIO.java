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
import java.util.ArrayList;
import java.util.List;

public class CompoundListIO<Elem> extends CompoundBaseIO<List<Elem>> {

    private final IO<Elem> io;

    public CompoundListIO(String name, IO<Elem> io) {
        super(name);
        this.io = io;
    }

    @Override
    protected List<Elem> loadEnd(STreeReader reader) throws IOException {
        ArrayList<Elem> b = new ArrayList<>();
        while (!reader.tryEndList()) {
            b.add(io.load(reader));
        }
        return b;
    }

    @Override
    protected void saveMiddle(STreeWriter writer, List<Elem> data) throws IOException {
        for (Elem e : data) {
            io.save(writer, e);
        }
    }

    @Override
    public boolean accepts(Object o) {
        if (!(o instanceof List)) {
            return false;
        }
        for (Object lo : (List) o) {
            if (!io.accepts(lo)) {
                return false;
            }
        }
        return true;
    }
}
