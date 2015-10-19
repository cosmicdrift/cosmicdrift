package cosmicdrift.dataio;

import java.io.IOException;

public abstract class WrappedCompoundIO<From, To> extends CompoundBaseIO<To> {

    private final CompoundBaseIO<From> io;

    public WrappedCompoundIO(CompoundBaseIO<From> loader) {
        super(loader.name);
        this.io = loader;
    }

    @Override
    protected To loadEnd(STreeReader reader) throws IOException {
        return convert(io.loadEnd(reader));
    }
    
    @Override
    protected void saveMiddle(STreeWriter writer, To object) throws IOException {
        io.saveMiddle(writer, deconvert(object));
    }

    protected abstract To convert(From load) throws IOException;

    protected abstract From deconvert(To load) throws IOException;

}
