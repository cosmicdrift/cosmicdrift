package io.github.cosmicdrift.cosmicdrift.components;

import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;

import java.util.Arrays;
import java.util.List;

public class ComponentRendererDirectioned extends Component {

    private final String prefix;
    private final String ctype;
    private final String[] lookup;

    private static final String[] defaultLookup = new String[]{
        "____", "l___", "_r__", "lr__", "__u_", "l_u_", "_ru_", "lru_",
        "___d", "l__d", "_r_d", "lr_d", "__ud", "l_ud", "_rud", "lrud"};

    public ComponentRendererDirectioned(String prefix, String ctype) {
        this.prefix = prefix;
        this.ctype = ctype;
        this.lookup = defaultLookup;
    }

    public ComponentRendererDirectioned(String prefix, String ctype, List<String> lookup) {
        this(prefix, ctype, lookup.toArray(new String[lookup.size()]));
    }

    private ComponentRendererDirectioned(String prefix, String ctype, String... lookup) {
        this.prefix = prefix;
        this.ctype = ctype;
        this.lookup = lookup;
        if (lookup.length != 16) {
            throw new IllegalArgumentException("Bad length of lookup: " + lookup.length);
        }
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        if (lookup == defaultLookup) {
            return new Object[]{prefix, ctype};
        } else {
            return new Object[]{prefix, ctype, Arrays.asList(lookup)};
        }
    }

    @Override
    public void onUpdateNearby(TileEntity ent) {
        Class<? extends Component> class_ = Registry.forName(ctype);
        int i = 0;
        for (TileEntity t : ent.getWorld().getTileEntities(ent.x - 1, ent.y)) {
            if (t.hasComponent(class_)) {
                i |= 1;
            }
        }
        for (TileEntity t : ent.getWorld().getTileEntities(ent.x + 1, ent.y)) {
            if (t.hasComponent(class_)) {
                i |= 2;
            }
        }
        for (TileEntity t : ent.getWorld().getTileEntities(ent.x, ent.y - 1)) {
            if (t.hasComponent(class_)) {
                i |= 4;
            }
        }
        for (TileEntity t : ent.getWorld().getTileEntities(ent.x, ent.y + 1)) {
            if (t.hasComponent(class_)) {
                i |= 8;
            }
        }
        ent.icon = prefix + lookup[i] + ".png";
    }
}
