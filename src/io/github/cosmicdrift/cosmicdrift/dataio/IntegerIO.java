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

public class IntegerIO implements IO<Integer> {

    @Override
    public Integer load(STreeReader reader) throws IOException {
        return reader.readInteger();
    }

    @Override
    public void save(STreeWriter writer, Integer object) throws IOException {
        writer.writeInteger(object);
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof Integer;
    }
}
