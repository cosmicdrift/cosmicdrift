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

public class CompoundAlternativesIO<O> implements IO<O> {

    private final CompoundBaseIO<? extends O>[] ios;

    public CompoundAlternativesIO(CompoundBaseIO<? extends O>... loaders) {
        this.ios = loaders;
    }

    @Override
    public O load(STreeReader reader) throws IOException {
        reader.beginList();
        String loaded = reader.readSymbol();
        for (CompoundBaseIO<? extends O> o : ios) {
            if (loaded.equals(o.name)) {
                return o.loadEnd(reader);
            }
        }
        throw new IOException("No alternative found for: " + loaded);
    }

    @Override
    public void save(STreeWriter writer, O object) throws IOException {
        writer.beginList();
        for (CompoundBaseIO<? extends O> io : ios) {
            if (io.accepts(object)) {
                if (io.name == null) {
                    throw new IOException("Name expected for alternative deselection.");
                }
                writer.writeSymbol(io.name);
                ((CompoundBaseIO) io).saveMiddle(writer, object); // TODO: Better way than a cast to allow for passing this.
                writer.endList();
                return;
            }
        }
        throw new IOException("No alternative found for: " + object + " (" + (object == null ? "null class" : object.getClass()) + ")");
    }

    @Override
    public boolean accepts(Object o) {
        for (CompoundBaseIO<? extends O> io : ios) {
            if (io.accepts(o)) {
                return true;
            }
        }
        return false;
    }
}
