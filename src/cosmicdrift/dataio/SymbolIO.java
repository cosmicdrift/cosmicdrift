package cosmicdrift.dataio;

import java.io.IOException;

public class SymbolIO implements IO<String> {

    @Override
    public String load(STreeReader reader) throws IOException {
        return reader.readSymbol();
    }

    @Override
    public void save(STreeWriter writer, String object) throws IOException {
        writer.writeSymbol(object);
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof String;
    }
}
