package cosmicdrift.dataio;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class CompoundArrayIO<Elem> extends CompoundBaseIO<Elem[]> {

    private final IO<Elem> io;
    private final Class<Elem> cls;

    public CompoundArrayIO(String name, IO<Elem> io, Class<Elem> cls) {
        super(name);
        this.io = io;
        this.cls = cls;
    }

    @Override
    protected Elem[] loadEnd(STreeReader reader) throws IOException {
        ArrayList<Elem> b = new ArrayList<>();
        while (!reader.tryEndList()) {
            b.add(io.load(reader));
        }
        return b.toArray((Elem[]) Array.newInstance(cls, b.size()));
    }

    @Override
    protected void saveMiddle(STreeWriter writer, Elem[] data) throws IOException {
        for (Elem e : data) {
            io.save(writer, e);
        }
    }

    @Override
    public boolean accepts(Object o) {
        if (!(o instanceof Object[]) || o.getClass().getComponentType() != cls) {
            return false;
        }
        for (Object e : (Object[]) o) {
            if (!io.accepts(e)) {
                return false;
            }
        }
        return true;
    }
}
