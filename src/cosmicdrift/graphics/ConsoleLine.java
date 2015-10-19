package cosmicdrift.graphics;

public class ConsoleLine {

    public final String line;
    public final long time;

    public ConsoleLine(String line) {
        this.line = line;
        this.time = System.currentTimeMillis();
    }

    public long age() {
        return System.currentTimeMillis() - this.time;
    }
}
