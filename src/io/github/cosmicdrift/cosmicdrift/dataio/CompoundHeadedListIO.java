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

public class CompoundHeadedListIO<Head, Body> extends CompoundBaseIO<CompoundHeadedList<Head, Body>> {

    private final IO<Head> head;
    private final IO<Body> body;

    public CompoundHeadedListIO(String name, IO<Head> head, IO<Body> body) {
        super(name);
        this.head = head;
        this.body = body;
    }

    @Override
    protected CompoundHeadedList<Head, Body> loadEnd(STreeReader reader) throws IOException {
        Head h = head.load(reader);
        ArrayList<Body> b = new ArrayList<>();
        while (!reader.tryEndList()) {
            b.add(body.load(reader));
        }
        return new CompoundHeadedList(h, b);
    }

    @Override
    protected void saveMiddle(STreeWriter writer, CompoundHeadedList<Head, Body> data) throws IOException {
        head.save(writer, data.head);
        for (Body b : data.body) {
            body.save(writer, b);
        }
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof CompoundHeadedList && head.accepts(((CompoundHeadedList) o).head) && body.accepts(((CompoundHeadedList) o).body);
    }
}
