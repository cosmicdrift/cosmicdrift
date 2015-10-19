package cosmicdrift.compents;

import cosmicdrift.items.ItemTileEntity;
import cosmicdrift.World;
import cosmicdrift.components.Component;
import cosmicdrift.items.Item;
import cosmicdrift.networks.Packet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TileEntity {

    public final TileEntityType type;
    public final int x, y;
    private final HashMap<String, Object> vars = new HashMap<>();
    public String icon;

    public World getWorld() {
        return type.world;
    }
    
    public TileEntity(TileEntityType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.vars.putAll(type.defaults);
        for (Component cmp : type.components) {
            cmp.initialize(this);
        }
    }

    public TileEntity(TileEntityType type, int x, int y, Map<String, Object> updates) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.vars.putAll(type.defaults);
        this.vars.putAll(updates);
        for (Component cmp : type.components) {
            cmp.initialize(this);
        }
    }
    
    TileEntity(TileEntityType type, int x, int y, Map<String, Object> updates, String icon) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.vars.putAll(type.defaults);
        this.vars.putAll(updates);
        this.icon = icon;
    }

    public void presave() {
        for (Component cmp : type.components) {
            cmp.presave(this);
        }
    }

    public void postsave() {
        for (Component cmp : type.components) {
            cmp.postsave(this);
        }
    }

    public String getIcon() {
        return icon;
    }
    
    public Map<String, Object> getVarView() {
        return Collections.unmodifiableMap(vars);
    }

    public boolean activate(double dist) {
        for (Component cmp : type.components) {
            if (cmp.onActivate(this, dist)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSolid() {
        Boolean b = get("solid");
        return b != null && b;
    }

    public void tick() {
        for (Component cmp : type.components) {
            cmp.onTick(this);
        }
    }

    public void onUpdateNearby() {
        for (Component cmp : type.components) {
            cmp.onUpdateNearby(this);
        }
    }

    public boolean hasComponent(Class<? extends Component> ctype) {
        for (Component c : type.components) {
            if (ctype.isInstance(c)) {
                return true;
            }
        }
        return false;
    }

    public Item getAsItemForDrop() {
        return new ItemTileEntity(type, icon, new HashMap<>(vars));
    }

    public void onAdded() {
        for (Component c : type.components) {
            c.onAdded(this);
        }
    }

    public void onRemove() {
        for (Component c : type.components) {
            c.onRemove(this);
        }
    }

    public <T extends Component> T getComponent(Class<T> cls) {
        for (Component c : type.components) {
            if (cls.isInstance(c)) {
                return cls.cast(c);
            }
        }
        return null;
    }

    public void message(Packet pkt) {
        for (Component cmp : type.components) { // TODO: do this better?
            cmp.onMessage(this, pkt);
        }
    }

    public <T> T get(String var) {
        return (T) vars.get(var);
    }

    public boolean set(String var, Object o) {
        if ("solid".equals(var) && o != null && (boolean) o) {
            // Deny if it would cause there to be two solid objects.
            if (getWorld().isSolid(x, y)) {
                return false;
            }
        }
        for (Component cmp : type.components) {
            if (cmp.beforeVariableChange(this, var, o)) {
                return false; // Allow components to deny variable changes.
            }
        }
        vars.put(var, o);
        for (Component cmp : type.components) {
            cmp.onVariableChange(this, var, o);
        }
        return true;
    }
}
