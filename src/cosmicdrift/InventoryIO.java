package cosmicdrift;

import cosmicdrift.dataio.CompressedListIO;
import cosmicdrift.dataio.IO;
import cosmicdrift.dataio.STreeReader;
import cosmicdrift.dataio.STreeWriter;
import cosmicdrift.items.Item;
import cosmicdrift.items.ItemIO;
import java.io.IOException;
import java.util.List;

public class InventoryIO implements IO<Inventory> {

    private final ItemIO itemIO;
    private final IO<List<Item>> invIO;

    public InventoryIO(World world) {
        this.itemIO = new ItemIO(world);
        this.invIO = new CompressedListIO<>(null, itemIO, Item.class);
    }

    @Override
    public Inventory load(STreeReader reader) throws IOException {
        reader.beginList();
        Inventory inv = new Inventory(reader.readInteger());
        inv.leftHand = itemIO.load(reader);
        inv.rightHand = itemIO.load(reader);
        inv.addAllFromLoad(invIO.load(reader));
        reader.endList();
        return inv;
    }

    @Override
    public void save(STreeWriter writer, Inventory object) throws IOException {
        writer.beginList();
        writer.writeInteger(object.slotCount);
        itemIO.save(writer, object.leftHand);
        itemIO.save(writer, object.rightHand);
        invIO.save(writer, object.inv);
        writer.endList();
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof Inventory;
    }
}
