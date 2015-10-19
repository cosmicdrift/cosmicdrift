package cosmicdrift.dataio;

import java.io.IOException;

public interface IO<X> {

    public X load(STreeReader reader) throws IOException;

    public void save(STreeWriter writer, X object) throws IOException;

    public boolean accepts(Object o);
}
