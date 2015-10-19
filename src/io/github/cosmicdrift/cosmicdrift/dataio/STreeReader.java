package io.github.cosmicdrift.cosmicdrift.dataio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

public class STreeReader implements AutoCloseable {

    public static STreeReader loadTextual(String filename) throws FileNotFoundException {
        return new STreeReader(new FileReader(filename));
    }

    public static STreeReader loadZipped(String filename) throws IOException {
        return new STreeReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));
    }

    private final STreeTokenizer input;

    public STreeReader(Reader input) {
        this.input = new STreeTokenizer(input);
    }

    public boolean isAtom() throws IOException {
        STreeToken next = input.next();
        return next != STreeToken.OPEN && next != STreeToken.CLOSE && next != STreeToken.EOF;
    }
    
    public boolean tryNull() throws IOException {
        return input.accept(STreeToken.NULL);
    }

    public boolean isSubexpr() throws IOException {
        return input.next() == STreeToken.OPEN;
    }

    public boolean isEOF() throws IOException {
        return input.next() == STreeToken.EOF;
    }

    public boolean hasNext() throws IOException {
        STreeToken next = input.next();
        return next != STreeToken.CLOSE && next != STreeToken.EOF;
    }

    public boolean tryBeginList() throws IOException {
        return input.accept(STreeToken.OPEN);
    }

    public void beginList() throws IOException {
        input.expect(STreeToken.OPEN);
    }

    public boolean tryEndList() throws IOException {
        return input.accept(STreeToken.CLOSE);
    }

    public void endList() throws IOException {
        input.expect(STreeToken.CLOSE);
    }

    public void expectNotEOF() throws IOException {
        if (input.accept(STreeToken.EOF)) {
            throw new IOException("Expected to not have EOF!");
        }
    }

    public void expectEOF() throws IOException {
        input.expect(STreeToken.EOF);
    }

    public Object readAtom() throws IOException {
        if (!isAtom()) {
            throw new IOException("Expected an atom but got: " + input.next());
        }
        return input.consume();
    }

    public String readString() throws IOException {
        return (String) input.expect(STreeToken.STRING);
    }

    public int readInteger() throws IOException {
        return (Integer) input.expect(STreeToken.INTEGER);
    }

    public double readReal() throws IOException {
        return (Double) input.expect(STreeToken.REAL);
    }

    public double readNumber() throws IOException {
        if (input.accept(STreeToken.INTEGER)) {
            return (Integer) input.getAssoc();
        } else {
            return readReal();
        }
    }

    public String readSymbol() throws IOException {
        return (String) input.expect(STreeToken.SYMBOL);
    }
    
    public boolean readBoolean() throws IOException {
        return (Boolean) input.expect(STreeToken.BOOLEAN);
    }

    public Object[] readAtomList() throws IOException {
        ArrayList<Object> out = new ArrayList<>();
        this.beginList();
        while (!this.tryEndList()) {
            out.add(readAtom());
        }
        return out.toArray();
    }

    public String[] readStringList() throws IOException {
        ArrayList<String> out = new ArrayList<>();
        this.beginList();
        while (!this.tryEndList()) {
            out.add(readString());
        }
        return out.toArray(new String[out.size()]);
    }

    public String[] readSymbolList() throws IOException {
        ArrayList<String> out = new ArrayList<>();
        this.beginList();
        while (!this.tryEndList()) {
            out.add(readSymbol());
        }
        return out.toArray(new String[out.size()]);
    }

    public int[] readIntegerList() throws IOException {
        int[] out = new int[16];
        int i = 0;
        this.beginList();
        while (!this.tryEndList()) {
            if (i >= out.length) {
                out = Arrays.copyOf(out, out.length * 2);
            }
            out[i++] = readInteger();
        }
        return Arrays.copyOf(out, i);
    }

    public double[] readRealList() throws IOException {
        double[] out = new double[16];
        int i = 0;
        this.beginList();
        while (!this.tryEndList()) {
            if (i >= out.length) {
                out = Arrays.copyOf(out, out.length * 2);
            }
            out[i++] = readReal();
        }
        return Arrays.copyOf(out, i);
    }

    public double[] readNumberList() throws IOException {
        double[] out = new double[16];
        int i = 0;
        this.beginList();
        while (!this.tryEndList()) {
            if (i >= out.length) {
                out = Arrays.copyOf(out, out.length * 2);
            }
            out[i++] = readNumber();
        }
        return Arrays.copyOf(out, i);
    }
    
    @Override
    public void close() throws IOException {
        input.close();
    }

    public String context() {
        return input.context();
    }
}
