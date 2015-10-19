package cosmicdrift.entities;

import cosmicdrift.World;
import cosmicdrift.dataio.CompoundAlternativesIO;
import cosmicdrift.dataio.IO;
import cosmicdrift.dataio.STreeReader;
import cosmicdrift.dataio.STreeWriter;
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
