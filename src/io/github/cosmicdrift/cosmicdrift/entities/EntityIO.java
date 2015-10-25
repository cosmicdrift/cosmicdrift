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
package io.github.cosmicdrift.cosmicdrift.entities;

import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.dataio.CompoundAlternativesIO;
import io.github.cosmicdrift.cosmicdrift.dataio.IO;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeReader;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeWriter;

import java.io.IOException;

public class EntityIO implements IO<Entity> {

    private final EntityPlayerIO entityPlayerIO;
    private final EntityItemIO entityItemIO;
    
    private final CompoundAlternativesIO<Entity> io;

    public EntityIO(World world) {
        this.entityPlayerIO = new EntityPlayerIO(world);
        this.entityItemIO = new EntityItemIO(world);
        this.io = new CompoundAlternativesIO<>(entityPlayerIO, entityItemIO);
    }

    @Override
    public Entity load(STreeReader reader) throws IOException {
        return io.load(reader);
    }

    @Override
    public void save(STreeWriter writer, Entity object) throws IOException {
        io.save(writer, object);
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof Entity;
    }
}
