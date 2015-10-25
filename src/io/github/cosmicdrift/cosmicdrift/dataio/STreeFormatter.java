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

import java.io.IOException;
import java.io.Writer;

public class STreeFormatter {

    private final Writer writer;
    private int layer = 0;
    private boolean needsPadding = false;
    private final boolean shouldIndent;

    public STreeFormatter(Writer writer, boolean shouldIndent) {
        this.writer = writer;
        this.shouldIndent = shouldIndent;
    }

    public void close() throws IOException {
        writer.close();
    }
    
    private void pad() throws IOException {
        if (needsPadding) {
            writer.write(' ');
            needsPadding = false;
        }
    }

    public void write(STreeToken t) throws IOException {
        write(t, null);
    }

    public void write(STreeToken t, Object associated) throws IOException {
        switch (t) {
            case OPEN:
                writer.write('\n');
                indent(layer);
                layer++;
                writer.write('(');
                break;
            case CLOSE:
                layer--;
                writer.write(')');
                break;
            case BOOLEAN:
                pad();
                writer.write(((Boolean) associated) ? "true" : "false");
                break;
            case INTEGER:
                pad();
                writer.write(Integer.toString((Integer) associated));
                break;
            case NULL:
                pad();
                writer.write("null");
                break;
            case REAL:
                pad();
                writer.write(Double.toString((Double) associated));
                break;
            case STRING:
                pad();
                writer.write(escapeString((String) associated));
                break;
            case SYMBOL:
                pad();
                writer.write(checkSymbol((String) associated));
                break;
            default:
                throw new IOException("Invalid token: " + t);
        }
        needsPadding = t != STreeToken.OPEN;
    }

    private void indent(int layer) throws IOException {
        if (shouldIndent) {
            while (layer-- > 0) {
                writer.write("    ");
            }
        }
    }

    private String checkSymbol(String string) throws IOException {
        if (string.matches("[ \t\n\r()\"]")) {
            throw new IOException("Invalid symbol as-is: " + string);
        }
        return string;
    }

    private String escapeString(String string) {
        StringBuilder out = new StringBuilder("\"");
        for (char c: string.toCharArray()) {
            out.append(STreeTokenizer.escape(c));
        }
        return out.append("\"").toString();
    }
}
