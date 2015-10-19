package cosmicdrift.compents;

import cosmicdrift.World;
import cosmicdrift.dataio.Compound2;
import cosmicdrift.dataio.Compound2IO;
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

public class TileEntityIO implements IO<TileEntity> {

    private final World world;
    private final IO<List<Compound2<String, Object>>> varIO = new CompoundListIO<>(null, new Compound2IO<>(null, new SymbolIO(), new ExpressionIO()));

    public TileEntityIO(World world) {
        this.world = world;
    }

    @Override
    public TileEntity load(STreeReader reader) throws IOException {
        reader.beginList();
        TileEntity out = new TileEntity(world.getTileEntityType(reader.readSymbol()), reader.readInteger(), reader.readInteger(), readVars(reader), reader.readString());
        reader.endList();
        return out;
    }

    private Map<String, Object> readVars(STreeReader reader) throws IOException {
        HashMap<String, Object> out = new HashMap<>();
        for (Compound2<String, Object> cmp : varIO.load(reader)) {
            out.put(cmp.a, cmp.b);
        }
        return out;
    }

    @Override
    public void save(STreeWriter writer, TileEntity object) throws IOException {
        writer.beginList();
        writer.writeSymbol(object.type.typename);
        writer.writeInteger(object.x);
        writer.writeInteger(object.y);
        ArrayList<Compound2<String, Object>> vlist = new ArrayList<>();
        for (Map.Entry<String, Object> ent : object.getVarView().entrySet()) {
            vlist.add(new Compound2<>(ent.getKey(), ent.getValue()));
        }
        varIO.save(writer, vlist);
        writer.writeString(object.icon);
        writer.endList();
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof TileEntity;
    }
}
