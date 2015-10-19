package cosmicdrift.items;

import cosmicdrift.World;
import cosmicdrift.compents.TileEntityType;
import cosmicdrift.dataio.Compound2;
import cosmicdrift.dataio.Compound2IO;
import cosmicdrift.dataio.CompoundBaseIO;
import cosmicdrift.dataio.CompoundListIO;
import cosmicdrift.dataio.ExpressionIO;
import cosmicdrift.dataio.IO;
import cosmicdrift.dataio.STreeReader;
import cosmicdrift.dataio.STreeWriter;
import cosmicdrift.dataio.SymbolIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemTileEntityIO extends CompoundBaseIO<ItemTileEntity> {

    private final IO<List<Compound2<String, Object>>> varIO = new CompoundListIO<>(null,
            new Compound2IO<>(null, new SymbolIO(), new ExpressionIO()));
    
    private final World world;

    public ItemTileEntityIO(World world) {
        super("tile-entity");
        this.world = world;
    }

    @Override
    protected ItemTileEntity loadEnd(STreeReader reader) throws IOException {
        String icon = reader.readString();
        TileEntityType type = world.getTileEntityType(reader.readSymbol());
        HashMap<String, Object> vars = new HashMap<>();
        for (Compound2<String, Object> var : varIO.load(reader)) {
            vars.put(var.a, var.b);
        }
        reader.endList();
        return new ItemTileEntity(type, icon, vars);
    }

    @Override
    protected void saveMiddle(STreeWriter writer, ItemTileEntity data) throws IOException {
        writer.writeString(data.getIcon());
        writer.writeSymbol(data.type.typename);
        ArrayList<Compound2<String, Object>> out = new ArrayList<>();
        for (Map.Entry<String, Object> entry : data.getVars().entrySet()) {
            out.add(new Compound2<>(entry.getKey(), entry.getValue()));
        }
        varIO.save(writer, out);
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof ItemTileEntity;
    }
}