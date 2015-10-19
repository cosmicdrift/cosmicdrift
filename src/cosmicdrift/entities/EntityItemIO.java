package cosmicdrift.entities;

import cosmicdrift.World;
import cosmicdrift.dataio.CompoundBaseIO;
import cosmicdrift.dataio.STreeReader;
import cosmicdrift.dataio.STreeWriter;
import cosmicdrift.items.ItemIO;
import java.io.IOException;

public class EntityItemIO extends CompoundBaseIO<EntityItem> {

    private final World world;
    private final ItemIO itemIO;

    public EntityItemIO(World world) {
        super("item");
        this.world = world;
        this.itemIO = new ItemIO(world);
    }
    
    @Override
    protected EntityItem loadEnd(STreeReader reader) throws IOException {
        EntityItem out = new EntityItem(reader.readInteger(), reader.readInteger(), itemIO.load(reader), world);
        reader.endList();
        return out;
    }

    @Override
    protected void saveMiddle(STreeWriter writer, EntityItem data) throws IOException {
        writer.writeInteger(data.x1);
        writer.writeInteger(data.y1);
        itemIO.save(writer, data.item);
    }

    @Override
    public boolean accepts(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}