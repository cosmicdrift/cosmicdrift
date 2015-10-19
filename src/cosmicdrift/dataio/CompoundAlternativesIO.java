package cosmicdrift.dataio;

import java.io.IOException;

public class CompoundAlternativesIO<O> implements IO<O> {

    private final CompoundBaseIO<? extends O>[] ios;

    public CompoundAlternativesIO(CompoundBaseIO<? extends O>... loaders) {
        this.ios = loaders;
    }

    @Override
    public O load(STreeReader reader) throws IOException {
        reader.beginList();
        String loaded = reader.readSymbol();
        for (CompoundBaseIO<? extends O> o : ios) {
            if (loaded.equals(o.name)) {
                return o.loadEnd(reader);
            }
        }
        throw new IOException("No alternative found for: " + loaded);
    }

    @Override
    public void save(STreeWriter writer, O object) throws IOException {
        writer.beginList();
        for (CompoundBaseIO<? extends O> io : ios) {
            if (io.accepts(object)) {
                if (io.name == null) {
                    throw new IOException("Name expected for alternative deselection.");
                }
                writer.writeSymbol(io.name);
                ((CompoundBaseIO) io).saveMiddle(writer, object); // TODO: Better way than a cast to allow for passing this.
                writer.endList();
                return;
            }
        }
        throw new IOException("No alternative found for: " + object + " (" + (object == null ? "null class" : object.getClass()) + ")");
    }

    @Override
    public boolean accepts(Object o) {
        for (CompoundBaseIO<? extends O> io : ios) {
            if (io.accepts(o)) {
                return true;
            }
        }
        return false;
    }
}
