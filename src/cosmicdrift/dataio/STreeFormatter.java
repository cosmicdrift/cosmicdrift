package cosmicdrift.dataio;

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
