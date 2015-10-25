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

public class Compound2IO<A, B> extends CompoundBaseIO<Compound2<A, B>> {

    private final IO<A> a;
    private final IO<B> b;

    public Compound2IO(String name, IO<A> a, IO<B> b) {
        super(name);
        this.a = a;
        this.b = b;
    }

    @Override
    protected Compound2<A, B> loadEnd(STreeReader reader) throws IOException {
        A la = a.load(reader);
        B lb = b.load(reader);
        reader.endList();
        return new Compound2(la, lb);
    }

    @Override
    protected void saveMiddle(STreeWriter writer, Compound2<A, B> data) throws IOException {
        a.save(writer, data.a);
        b.save(writer, data.b);
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof Compound2 && a.accepts(((Compound2) o).a) && b.accepts(((Compound2) o).b);
    }
}
