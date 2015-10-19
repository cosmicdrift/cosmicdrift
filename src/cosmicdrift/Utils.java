package cosmicdrift;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Utils {

    public static <T> Iterable<T> joinIterables(final Iterable<T>... iterables) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                Iterator<T>[] iterators = new Iterator[iterables.length];
                for (int i = 0; i < iterators.length; i++) {
                    iterators[i] = iterables[i].iterator();
                }
                return joinIterators(iterators);
            }
        };
    }

    public static <T> Iterator<T> joinIterators(final Iterator<T>... iterators) {
        return new Iterator<T>() {
            private int next = 0;

            @Override
            public boolean hasNext() {
                while (next < iterators.length && !iterators[next].hasNext()) {
                    next++;
                }
                return next < iterators.length;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return iterators[next].next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not implemented for joined iterators.");
            }
        };
    }

    private static byte getFromHex(char msb, char lsb) throws IOException {
        return (byte) ((getFromHex(msb) << 4) | getFromHex(lsb));
    }

    private static byte getFromHex(char c) throws IOException {
        if (c >= '0' && c <= '9') {
            return (byte) (c - '0');
        } else if (c >= 'a' && c <= 'f') {
            return (byte) (10 + c - 'a');
        } else if (c >= 'A' && c <= 'F') {
            return (byte) (10 + c - 'A');
        } else {
            throw new IOException("Bad hex digit: " + (int) c);
        }
    }

    public static byte[] loadHex(StringBuilder sb) throws IOException {
        int i;
        while ((i = sb.indexOf("\n")) != -1) {
            sb.deleteCharAt(i);
        }
        while ((i = sb.indexOf("\r")) != -1) {
            sb.deleteCharAt(i);
        }
        while ((i = sb.indexOf(" ")) != -1) {
            sb.deleteCharAt(i);
        }
        byte[] out = new byte[sb.length() / 2];
        for (i = 0; i < out.length; i++) {
            char msb = sb.charAt(i << 1);
            char lsb = sb.charAt((i << 1) | 1);
            out[i] = getFromHex(msb, lsb);
        }
        return out;
    }

    public static String saveHex(byte[] hex) {
        StringBuilder out = new StringBuilder(hex.length * 2 + hex.length / 64 + 8);
        for (int i = 0; i < hex.length; i++) {
            byte b = hex[i];
            out.append(Integer.toHexString((b >> 4) & 15)).append(Integer.toHexString(b & 15));
            if ((i & 63) == 0) {
                out.append("\n");
            }
        }
        return out.toString();
    }
}
