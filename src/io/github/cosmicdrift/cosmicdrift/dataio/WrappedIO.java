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

public abstract class WrappedIO<From, To> implements IO<To> {

    private final IO<From> io;

    public WrappedIO(IO<From> loader) {
        this.io = loader;
    }

    protected abstract To convert(From from);

    protected abstract From deconvert(To from);

    @Override
    public To load(STreeReader reader) throws IOException {
        return convert(io.load(reader));
    }

    @Override
    public void save(STreeWriter writer, To to) throws IOException {
        io.save(writer, deconvert(to));
    }

}
