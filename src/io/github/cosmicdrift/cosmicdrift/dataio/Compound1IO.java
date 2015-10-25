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

public class Compound1IO<O> extends CompoundBaseIO<O> {

    private final IO<O> io;

    public Compound1IO(String name, IO<O> loader) {
        super(name);
        this.io = loader;
    }

    @Override
    protected O loadEnd(STreeReader reader) throws IOException {
        O out = io.load(reader);
        reader.endList();
        return out;
    }

    @Override
    protected void saveMiddle(STreeWriter writer, O data) throws IOException {
        io.save(writer, data);
    }

    @Override
    public boolean accepts(Object o) {
        return io.accepts(o);
    }
}
