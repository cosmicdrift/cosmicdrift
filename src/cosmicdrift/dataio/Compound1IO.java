package cosmicdrift.dataio;

import java.io.IOException;

public class Compound1IO<O> extends CompoundBaseIO<O> {

    private final IO<O> io;

    public Compound1IO(String name, IO<O> loader) {
        super(name);
        this.io = loader;
    }

    @Override
    protected O loadEnd(STreeReader reader) throws IOException {
        O out = io.load(reader);
        reader.endList();
        return out;
    }

    @Override
    protected void saveMiddle(STreeWriter writer, O data) throws IOException {
        io.save(writer, data);
    }

    @Override
    public boolean accepts(Object o) {
        return io.accepts(o);
    }
}
