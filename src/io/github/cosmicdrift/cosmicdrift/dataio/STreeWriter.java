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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.zip.GZIPOutputStream;

public class STreeWriter implements AutoCloseable {

    public static STreeWriter saveTextual(String filename) throws IOException {
        return new STreeWriter(new BufferedWriter(new FileWriter(filename)), true);
    }
    
    public static STreeWriter saveZipped(String filename) throws IOException {
        return new STreeWriter(new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(filename)))), false);
    }

    private final STreeFormatter output;

    public STreeWriter(Writer writer, boolean includeIndent) {
        this.output = new STreeFormatter(writer, includeIndent);
    }

    public void beginList() throws IOException {
        output.write(STreeToken.OPEN);
    }

    public void endList() throws IOException {
        output.write(STreeToken.CLOSE);
    }

    public void writeSymbol(String name) throws IOException {
        output.write(STreeToken.SYMBOL, name);
    }

    public static boolean isAtom(Object object) {
        return object == null || object instanceof String || object instanceof Integer || object instanceof Double || object instanceof Boolean;
    }

    public void writeAtom(Object object, boolean asSymbol) throws IOException {
        if (object instanceof String) {
            output.write(asSymbol ? STreeToken.SYMBOL : STreeToken.STRING, object);
        } else if (object instanceof Integer) {
            output.write(STreeToken.INTEGER, object);
        } else if (object instanceof Double) {
            output.write(STreeToken.REAL, object);
        } else if (object instanceof Boolean) {
            output.write(STreeToken.BOOLEAN, object);
        } else if (object == null) {
            output.write(STreeToken.NULL);
        } else {
            throw new IOException("Not an atom: " + object.getClass());
        }
    }

    public void writeInteger(int value) throws IOException {
        output.write(STreeToken.INTEGER, value);
    }

    public void writeNull() throws IOException {
        output.write(STreeToken.NULL);
    }

    public void writeBoolean(boolean value) throws IOException {
        output.write(STreeToken.BOOLEAN, value);
    }

    public void writeString(String value) throws IOException {
        output.write(STreeToken.STRING, value);
    }

    @Override
    public void close() throws IOException {
        output.close();
    }
}
