package cosmicdrift.dataio;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Requires that Elem can be used as a HashMap key.
public class CompressedListIO<Elem> extends CompoundBaseIO<List<Elem>> {

    private final IO<Elem[]> indexIO;
    private final CompoundArrayIO<Integer> encodeIO;
    private final Class<Elem> etype;

    public CompressedListIO(String name, IO<Elem> elemIO, Class<Elem> type) {
        super(name);
        indexIO = new CompoundArrayIO<>("index", elemIO, type);
        encodeIO = new CompoundArrayIO<>("data", new IntegerIO(), Integer.class);
        etype = type;
    }

    @Override
    protected List<Elem> loadEnd(STreeReader reader) throws IOException {
        Elem[] index = indexIO.load(reader);
        Integer[] contents = encodeIO.load(reader);
        ArrayList out = new ArrayList(contents.length);
        for (Integer lkup : contents) {
            if (lkup < 0 || lkup >= index.length) {
                throw new IOException("Compressed array lookup index out of bounds");
            }
            out.add(index[lkup]);
        }
        reader.endList();
        return out;
    }

    @Override
    protected void saveMiddle(STreeWriter writer, List<Elem> data) throws IOException {
        HashMap<Elem, Integer> index = new HashMap<>();
        ArrayList<Elem> lookup = new ArrayList<>();
        Integer[] out = new Integer[data.size()];
        int i = 0;
        for (Elem t : data) {
            if (!index.containsKey(t)) {
                index.put(t, lookup.size());
                lookup.add(t);
            }
            out[i++] = index.get(t);
        }
        indexIO.save(writer, lookup.toArray((Elem[]) Array.newInstance(etype, lookup.size())));
        encodeIO.save(writer, out);
    }

    @Override
    public boolean accepts(Object o) {
        return indexIO.accepts(o);
    }
}
