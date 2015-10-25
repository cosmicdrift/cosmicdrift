/*
    Cosmic Drift is a computer game about building simulated space stations.
    Copyright (C) 2014-2015 Colby Skeggs.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
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
