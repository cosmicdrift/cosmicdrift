package io.github.cosmicdrift.cosmicdrift.components;

import com.google.gson.*;
import io.github.cosmicdrift.cosmicdrift.utils.Registries;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

public class CompTypeAdapter implements JsonSerializer<Component>, JsonDeserializer<Component> {

    private static <T> Constructor<T> getConstructorN(Class<T> class_, int args) {
        Constructor<T> candidate = null;
        for (Constructor<?> constructor : class_.getConstructors()) {
            if (constructor.getParameterTypes().length == args) {
                if (candidate == null) {
                    candidate = (Constructor<T>) constructor;
                } else {
                    throw new RuntimeException("Class had multiple constructors with " + args + " arguments: " + class_);
                }
            }
        }
        if (candidate == null) {
            throw new RuntimeException("Class had no constructor with " + args + " arguments: " + class_);
        }
        return candidate;
    }

    @Override
    public Component deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray array = jsonElement.getAsJsonArray();
        if (array.size() < 1) {
            throw new JsonParseException("Not a valid serialized component!");
        }
        Class<? extends Component> cc = Registries.componentRegistry.forName(array.get(0).getAsString());
        Object[] args = new Object[array.size() - 1];
        Constructor<? extends Component> comp = getConstructorN(cc, args.length);
        Type[] types = comp.getGenericParameterTypes();
        for (int i = 0; i < args.length; i++) {
            args[i] = jsonDeserializationContext.deserialize(array.get(i + 1), types[i]);
        }
        try {
            return comp.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new JsonParseException("Failed to deserialize component: " + cc + " with " + args.length + " arguments.");
        }
    }

    @Override
    public JsonElement serialize(Component component, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonArray array = new JsonArray();
        array.add(Registries.componentRegistry.forClass(component.getClass()));
        Object[] args = component.saveAsConstructorArguments();
        Constructor<? extends Component> comp = getConstructorN(component.getClass(), args.length);
        Class<?>[] types = comp.getParameterTypes();
        for (int i = 0; i < args.length; i++) {
            Object o = args[i];
            Class<?> t = types[i];
            switch (t.getName()) {
                case "byte":
                    t = Boolean.class;
                    break;
                case "char":
                    t = Character.class;
                    break;
                case "short":
                    t = Short.class;
                    break;
                case "int":
                    t = Integer.class;
                    break;
                case "long":
                    t = Long.class;
                    break;
                case "boolean":
                    t = Boolean.class;
                    break;
                case "float":
                    t = Float.class;
                    break;
                case "double":
                    t = Double.class;
                    break;
            }
            if (o != null && !t.isInstance(o)) {
                throw new RuntimeException("saveAsConstructorArguments() provided result incompatible with constructor in " + component.getClass() + " on parameter " + i + " (" + args.length + " total): " + (o == null ? "null" : o.getClass()) + " instead of " + t);
            }
            array.add(jsonSerializationContext.serialize(o));
        }
        return array;
    }
}
