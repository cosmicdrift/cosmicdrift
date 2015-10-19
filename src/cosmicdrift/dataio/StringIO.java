package cosmicdrift.dataio;

import java.io.IOException;

public class StringIO implements IO<String> {

    @Override
    public String load(STreeReader reader) throws IOException {
        return reader.readString();
    }

    @Override
    public void save(STreeWriter writer, String object) throws IOException {
        writer.writeString(object);
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof String;
    }
}
