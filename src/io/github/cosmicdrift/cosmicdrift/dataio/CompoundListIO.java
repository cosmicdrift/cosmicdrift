package io.github.cosmicdrift.cosmicdrift.dataio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CompoundListIO<Elem> extends CompoundBaseIO<List<Elem>> {

    private final IO<Elem> io;

    public CompoundListIO(String name, IO<Elem> io) {
        super(name);
        this.io = io;
    }

    @Override
    protected List<Elem> loadEnd(STreeReader reader) throws IOException {
        ArrayList<Elem> b = new ArrayList<>();
        while (!reader.tryEndList()) {
            b.add(io.load(reader));
        }
        return b;
    }

    @Override
    protected void saveMiddle(STreeWriter writer, List<Elem> data) throws IOException {
        for (Elem e : data) {
            io.save(writer, e);
        }
    }

    @Override
    public boolean accepts(Object o) {
        if (!(o instanceof List)) {
            return false;
        }
        for (Object lo : (List) o) {
            if (!io.accepts(lo)) {
                return false;
            }
        }
        return true;
    }
}
