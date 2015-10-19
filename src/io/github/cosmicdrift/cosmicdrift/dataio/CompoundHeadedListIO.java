package io.github.cosmicdrift.cosmicdrift.dataio;

import java.io.IOException;
import java.util.ArrayList;

public class CompoundHeadedListIO<Head, Body> extends CompoundBaseIO<CompoundHeadedList<Head, Body>> {

    private final IO<Head> head;
    private final IO<Body> body;

    public CompoundHeadedListIO(String name, IO<Head> head, IO<Body> body) {
        super(name);
        this.head = head;
        this.body = body;
    }

    @Override
    protected CompoundHeadedList<Head, Body> loadEnd(STreeReader reader) throws IOException {
        Head h = head.load(reader);
        ArrayList<Body> b = new ArrayList<>();
        while (!reader.tryEndList()) {
            b.add(body.load(reader));
        }
        return new CompoundHeadedList(h, b);
    }

    @Override
    protected void saveMiddle(STreeWriter writer, CompoundHeadedList<Head, Body> data) throws IOException {
        head.save(writer, data.head);
        for (Body b : data.body) {
            body.save(writer, b);
        }
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof CompoundHeadedList && head.accepts(((CompoundHeadedList) o).head) && body.accepts(((CompoundHeadedList) o).body);
    }
}
