package io.github.cosmicdrift.cosmicdrift.dataio;

import java.io.IOException;

public class IntegerIO implements IO<Integer> {

    @Override
    public Integer load(STreeReader reader) throws IOException {
        return reader.readInteger();
    }

    @Override
    public void save(STreeWriter writer, Integer object) throws IOException {
        writer.writeInteger(object);
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof Integer;
    }
}
