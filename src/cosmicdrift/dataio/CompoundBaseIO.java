package cosmicdrift.dataio;

import java.io.IOException;

public abstract class CompoundBaseIO<O> implements IO<O> {

    final String name;

    public CompoundBaseIO(String name) {
        this.name = name;
    }

    @Override
    public final O load(STreeReader reader) throws IOException {
        reader.beginList();
        if (name != null) {
            String loaded = reader.readSymbol();
            if (!name.equals(loaded)) {
                throw new IOException("Expected compound name " + name + " but got " + loaded + " in " + reader.context());
            }
        }
        return loadEnd(reader);
    }
    
    @Override
    public final void save(STreeWriter writer, O data) throws IOException {
        writer.beginList();
        if (name != null) {
            writer.writeSymbol(name);
        }
        saveMiddle(writer, data);
        writer.endList();
    }

    // Accessed directly from CompoundAltsLoader!
    protected abstract O loadEnd(STreeReader reader) throws IOException;

    protected abstract void saveMiddle(STreeWriter writer, O data) throws IOException;

}
