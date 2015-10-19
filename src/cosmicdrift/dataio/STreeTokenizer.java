package cosmicdrift.dataio;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

public class STreeTokenizer implements AutoCloseable {

    private STreeToken next;
    private Object associated;
    private final Reader input;
    private final CharBuffer subsequent = CharBuffer.allocate(64);
    private String previous = "";

    public STreeTokenizer(Reader input) {
        this.input = input;
        subsequent.limit(0);
    }

    public String context() {
        return "<" + previous + subsequent + ">";
    }

    public STreeToken next() throws IOException {
        while (next == null) {
            parse();
        }
        return next;
    }

    public Object consume() throws IOException {
        next = null;
        return associated;
    }

    public boolean accept(STreeToken n) throws IOException {
        if (next() == n) {
            consume();
            return true;
        } else {
            return false;
        }
    }

    public Object expect(STreeToken n) throws IOException {
        STreeToken found = next();
        if (found != n) {
            throw new IOException("Expected token " + n + " but got token " + found + "/" + associated);
        }
        return consume();
    }

    public Object getAssoc() {
        return associated;
    }

    private Character nextChar() throws IOException {
        if (!subsequent.hasRemaining()) {
            previous = subsequent.toString();
            subsequent.rewind();
            subsequent.limit(subsequent.capacity());
            int out = input.read(subsequent);
            if (out == -1) {
                return null;
            }
            subsequent.flip();
            if (!subsequent.hasRemaining()) {
                throw new IOException("No new data from read!");
            }
        }
        return subsequent.get();
    }

    public void parse() throws IOException {
        if (next != null) {
            return;
        }
        associated = null;
        Character first = nextChar();
        if (first == null) {
            next = STreeToken.EOF;
            return;
        }
        switch (first) {
            case ' ':
            case '\t':
            case '\n':
            case '\r':
                break;
            case ';': // comment
                while (first != '\n') {
                    first = nextChar();
                }
                subsequent.position(subsequent.position() - 1);
                break;
            case '(':
                next = STreeToken.OPEN;
                break;
            case ')':
                next = STreeToken.CLOSE;
                break;
            case '"':
                next = STreeToken.STRING;
                associated = parseString();
                break;
            default:
                String s = parseSymbol(first);
                if ("true".equals(s)) {
                    associated = true;
                    next = STreeToken.BOOLEAN;
                } else if ("false".equals(s)) {
                    associated = false;
                    next = STreeToken.BOOLEAN;
                } else if ("null".equals(s)) {
                    next = STreeToken.NULL;
                } else {
                    try {
                        associated = Integer.parseInt(s);
                        next = STreeToken.INTEGER;
                    } catch (NumberFormatException ex) {
                        try {
                            associated = Double.parseDouble(s);
                            next = STreeToken.REAL;
                        } catch (NumberFormatException ex2) {
                            associated = s;
                            next = STreeToken.SYMBOL;
                        }
                    }
                }
                break;
        }
    }

    public static char unescape(char c) {
        switch (c) {
            case 'a':
                return 7;
            case 'b':
                return 8;
            case 't':
                return '\t';
            case 'n':
                return '\n';
            case 'v':
                return 11;
            case 'f':
                return 12;
            case 'r':
                return '\r';
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            default:
                return c;
        }
    }

    public static String escape(char c) {
        switch (c) {
            case 7:
                return "\\a";
            case 8:
                return "\\b";
            case '\t':
                return "\\t";
            case '\n':
                return "\\n";
            case 11:
                return "\\v";
            case 12:
                return "\\f";
            case '\r':
                return "\\r";
            case 0:
                return "\\0";
            case 1:
                return "\\1";
            case 2:
                return "\\2";
            case 3:
                return "\\3";
            case 4:
                return "\\4";
            case 5:
                return "\\5";
            case 6:
                return "\\6";
            default:
                return Character.toString(c);
        }
    }

    private String parseString() throws IOException {
        boolean isEscape = false;
        StringBuilder out = new StringBuilder();
        while (true) {
            Character nc = nextChar();
            if (nc == null) {
                throw new IOException("Unexpected EOF while parsing string literal!");
            }
            if (isEscape) {
                out.append(unescape(nc));
                isEscape = false;
            } else if (nc == '\\') {
                isEscape = true;
            } else if (nc == '"') {
                break;
            } else {
                out.append(nc);
            }
        }
        return out.toString();
    }

    private String parseSymbol(char first) throws IOException {
        StringBuilder out = new StringBuilder().append(first);
        while (true) {
            Character nc = nextChar();
            if (nc == null) {
                return out.toString();
            }
            switch (nc) {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                case '(':
                case ')':
                case '"':
                    subsequent.position(subsequent.position() - 1);
                    return out.toString();
            }
            out.append(nc);
        }
    }

    public void close() throws IOException {
        input.close();
    }
}
