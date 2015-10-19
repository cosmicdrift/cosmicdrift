package cosmicdrift.dataio;

import java.io.IOException;

public abstract class WrappedIO<From, To> implements IO<To> {

    private final IO<From> io;

    public WrappedIO(IO<From> loader) {
        this.io = loader;
    }

    protected abstract To convert(From from);

    protected abstract From deconvert(To from);

    @Override
    public To load(STreeReader reader) throws IOException {
        return convert(io.load(reader));
    }

    @Override
    public void save(STreeWriter writer, To to) throws IOException {
        io.save(writer, deconvert(to));
    }

}
