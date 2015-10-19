package cosmicdrift.dataio;

import cosmicdrift.components.Component;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class ComponentIO extends CompoundBaseIO<Component> {

    private final IO<Object> expr = new ExpressionIO();

    public ComponentIO(String name) {
        super(name);
    }

    @Override
    protected Component loadEnd(STreeReader reader) throws IOException {
        String sym = reader.readSymbol();
        ArrayList<Object> exprs = new ArrayList<>();
        ArrayList<Class<?>> types = new ArrayList<>();
        while (!reader.tryEndList()) {
            Object line = expr.load(reader);
            exprs.add(line);
            types.add(line == null ? null : line.getClass());
        }
        Class<? extends Component> cls = loadComponentRef(sym);
        try {
            Constructor<?> found = null;
            Constructor<?>[] constr = cls.getConstructors();
            for (Constructor<?> c : constr) {
                if (c.getParameterTypes().length == exprs.size()) {
                    if (found != null) {
                        throw new NoSuchMethodException("Multiple candidate methods: " + found + " and " + c);
                    }
                    found = c;
                }
            }
            if (found == null) {
                throw new NoSuchMethodException("No candidate methods");
            }
            // TODO: Better matching?
            return (Component) found.newInstance(exprs.toArray());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            throw new IOException("Could not load component: " + sym + " with arguments " + types + ":" + exprs, ex);
        }
    }

    @Override
    protected void saveMiddle(STreeWriter writer, Component data) throws IOException {
        writer.writeSymbol(saveComponentRef(data.getClass()));
        for (Object o : data.saveAsConstructorArguments()) {
            expr.save(writer, o);
        }
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof Component;
    }

    private static final String clsprefix = "cosmicdrift.components.Component";

    public static Class<? extends Component> loadComponentRef(String sym) throws IOException {
        try {
            return Class.forName(clsprefix + sym).asSubclass(Component.class);
        } catch (ClassNotFoundException ex) {
            throw new IOException("Could not load component type: " + sym, ex);
        }
    }

    public static String saveComponentRef(Class<? extends Component> load) throws IOException {
        String clsname = load.getName();
        if (!clsname.startsWith(clsprefix)) {
            throw new IOException("Could not save component type: " + load);
        }
        return clsname.substring(clsprefix.length());
    }
}
