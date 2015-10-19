package cosmicdrift.dataio;

import java.io.IOException;

public class Compound2IO<A, B> extends CompoundBaseIO<Compound2<A, B>> {

    private final IO<A> a;
    private final IO<B> b;

    public Compound2IO(String name, IO<A> a, IO<B> b) {
        super(name);
        this.a = a;
        this.b = b;
    }

    @Override
    protected Compound2<A, B> loadEnd(STreeReader reader) throws IOException {
        A la = a.load(reader);
        B lb = b.load(reader);
        reader.endList();
        return new Compound2(la, lb);
    }

    @Override
    protected void saveMiddle(STreeWriter writer, Compound2<A, B> data) throws IOException {
        a.save(writer, data.a);
        b.save(writer, data.b);
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof Compound2 && a.accepts(((Compound2) o).a) && b.accepts(((Compound2) o).b);
    }
}
