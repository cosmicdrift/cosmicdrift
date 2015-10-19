package cosmicdrift.items;

import cosmicdrift.World;
import cosmicdrift.dataio.CompoundAlternativesIO;
import cosmicdrift.dataio.CompoundBaseIO;
import cosmicdrift.dataio.IO;
import cosmicdrift.dataio.STreeReader;
import cosmicdrift.dataio.STreeWriter;
import java.io.IOException;

public class ItemIO implements IO<Item> {

    private final ItemTileEntityIO itemTileEntityIO;

    private final CompoundAlternativesIO<Item> io;

    public ItemIO(World world) {
        this.itemTileEntityIO = new ItemTileEntityIO(world);
        this.io = new CompoundAlternativesIO<>((CompoundBaseIO<? extends Item>) itemTileEntityIO);
    }

    @Override
    public Item load(STreeReader reader) throws IOException {
        if (reader.isAtom()) {
            if (reader.tryNull()) {
                return null;
            }
            String sym = reader.readSymbol();
            switch (sym) {
                case "fluid-monitor":
                    return new ItemFluidsMonitor();
                case "network-monitor":
                    return new ItemNetworkMonitor();
                case "plating":
                    return new ItemPlating();
                case "wrench":
                    return new ItemWrench();
                default:
                    throw new IOException("No such prefab: " + sym);
            }
        }
        return io.load(reader);
    }

    @Override
    public void save(STreeWriter writer, Item object) throws IOException {
        if (object instanceof ItemFluidsMonitor) {
            writer.writeSymbol("fluid-monitor");
        } else if (object instanceof ItemNetworkMonitor) {
            writer.writeSymbol("network-monitor");
        } else if (object instanceof ItemPlating) {
            writer.writeSymbol("plating");
        } else if (object instanceof ItemWrench) {
            writer.writeSymbol("wrench");
        } else if (object == null) {
            writer.writeNull();
        } else {
            io.save(writer, object);
        }
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof Item;
    }
}
