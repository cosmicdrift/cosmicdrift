package io.github.cosmicdrift.cosmicdrift.dataio;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

// Requires that Elem can be used as a HashMap key.
public class CompressedArrayIO<Elem> extends CompoundBaseIO<Elem[]> {

    private final IO<Elem[]> indexIO;
    private final CompoundArrayIO<Integer> encodeIO;
    private final Class<Elem> etype;

    public CompressedArrayIO(String name, IO<Elem> elemIO, Class<Elem> type) {
        super(name);
        indexIO = new CompoundArrayIO<>("index", elemIO, type);
        encodeIO = new CompoundArrayIO<>("data", new IntegerIO(), Integer.class);
        etype = type;
    }

    @Override
    protected Elem[] loadEnd(STreeReader reader) throws IOException {
        Elem[] index = indexIO.load(reader);
        Integer[] contents = encodeIO.load(reader);
        Elem[] out = (Elem[]) Array.newInstance(etype, contents.length);
        for (int i = 0; i < contents.length; i++) {
            int lkup = contents[i];
            if (lkup < 0 || lkup >= index.length) {
                throw new IOException("Compressed array lookup index out of bounds");
            }
            out[i] = index[lkup];
        }
        reader.endList();
        return out;
    }

    @Override
    protected void saveMiddle(STreeWriter writer, Elem[] data) throws IOException {
        HashMap<Elem, Integer> index = new HashMap<>();
        ArrayList<Elem> lookup = new ArrayList<>();
        Integer[] out = new Integer[data.length];
        for (int i = 0; i < data.length; i++) {
            Elem t = data[i];
            if (!index.containsKey(t)) {
                index.put(t, lookup.size());
                lookup.add(t);
            }
            out[i] = index.get(t);
        }
        indexIO.save(writer, lookup.toArray((Elem[]) Array.newInstance(etype, lookup.size())));
        encodeIO.save(writer, out);
    }

    @Override
    public boolean accepts(Object o) {
        return indexIO.accepts(o);
    }
}
