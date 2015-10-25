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

public abstract class CompoundBaseIO<O> implements IO<O> {

    final String name;

    public CompoundBaseIO(String name) {
        this.name = name;
    }

    @Override
    public final O load(STreeReader reader) throws IOException {
        reader.beginList();
        if (name != null) {
            String loaded = reader.readSymbol();
            if (!name.equals(loaded)) {
                throw new IOException("Expected compound name " + name + " but got " + loaded + " in " + reader.context());
            }
        }
        return loadEnd(reader);
    }
    
    @Override
    public final void save(STreeWriter writer, O data) throws IOException {
        writer.beginList();
        if (name != null) {
            writer.writeSymbol(name);
        }
        saveMiddle(writer, data);
        writer.endList();
    }

    // Accessed directly from CompoundAltsLoader!
    protected abstract O loadEnd(STreeReader reader) throws IOException;

    protected abstract void saveMiddle(STreeWriter writer, O data) throws IOException;

}
