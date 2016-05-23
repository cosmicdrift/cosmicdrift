/*
    Cosmic Drift is a computer game about building simulated space stations.
    Copyright (C) 2014-2015 Cel Skeggs and Christopher Quisling.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.cosmicdrift.cosmicdrift.compents;

import io.github.cosmicdrift.cosmicdrift.items.ItemTileEntity;
import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.components.Component;
import io.github.cosmicdrift.cosmicdrift.items.Item;
import io.github.cosmicdrift.cosmicdrift.networks.Packet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TileEntity {

    public final World world;
    public final TileEntityType type;
    public final int x, y;
    private final HashMap<String, Object> vars = new HashMap<>();
    public String icon;

    public World getWorld() {
        return world;
    }

    public TileEntity(World world, TileEntityType type, int x, int y) {
        this.world = world;
        this.type = type;
        this.x = x;
        this.y = y;
        this.vars.putAll(type.defaults);
        for (Component cmp : type.components) {
            cmp.initialize(this);
        }
    }

    public TileEntity(World world, TileEntityType type, int x, int y, Map<String, Object> updates) {
        this.world = world;
        this.type = type;
        this.x = x;
        this.y = y;
        this.vars.putAll(type.defaults);
        this.vars.putAll(updates);
        for (Component cmp : type.components) {
            cmp.initialize(this);
        }
    }

    TileEntity(World world, TileEntityType type, int x, int y, Map<String, Object> updates, String icon) {
        this.world = world;
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

    public void onUserEvent(String eventName) {
        for (Component cmp : type.components) {
            cmp.onUserEvent(this, eventName);
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

    @Override
    public String toString() {
        return "tileent:" + type.typename + "@" + Integer.toHexString(hashCode());
    }
}
