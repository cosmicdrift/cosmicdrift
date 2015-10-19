package io.github.cosmicdrift.cosmicdrift.dataio;

import io.github.cosmicdrift.cosmicdrift.Utils;

import java.io.IOException;

public class ExpressionIO implements IO<Object> {

    private final CompoundAlternativesIO<?> alt = new CompoundAlternativesIO<>(
            new WrappedCompoundIO<String, PresetRef>(new Compound1IO("preset-ref", new SymbolIO())) {
                @Override
                protected PresetRef convert(String load) throws IOException {
                    return new PresetRef(load);
                }

                @Override
                protected String deconvert(PresetRef load) throws IOException {
                    return load.getReference();
                }

                @Override
                public boolean accepts(Object o) {
                    return o instanceof PresetRef;
                }
            },
            new WrappedCompoundIO<String, byte[]>(new Compound1IO("byte-array", new StringIO())) {

                @Override
                protected byte[] convert(String load) throws IOException {
                    return Utils.loadHex(new StringBuilder(load));
                }

                @Override
                protected String deconvert(byte[] load) throws IOException {
                    return Utils.saveHex(load);
                }

                @Override
                public boolean accepts(Object o) {
                    return o instanceof byte[];
                }
            },
            new CompoundListIO("list", ExpressionIO.this)
    );

    @Override
    public Object load(STreeReader reader) throws IOException {
        if (reader.isAtom()) {
            return reader.readAtom();
        } else {
            return alt.load(reader);
        }
    }

    @Override
    public void save(STreeWriter writer, Object object) throws IOException {
        if (STreeWriter.isAtom(object)) {
            writer.writeAtom(object, false);
        } else {
            ((IO) alt).save(writer, object); // TODO: Better way than a cast to allow for passing this.
        }
    }

    @Override
    public boolean accepts(Object o) {
        return STreeWriter.isAtom(o) || alt.accepts(o);
    }
}
