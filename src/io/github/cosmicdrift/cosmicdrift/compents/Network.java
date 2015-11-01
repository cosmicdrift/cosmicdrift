/*
    Cosmic Drift is a computer game about building simulated space stations.
    Copyright (C) 2014-2015 Colby Skeggs and Christopher Quisling.

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

import io.github.cosmicdrift.cosmicdrift.utils.Utils;
import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.components.ComponentNetwork;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class Network {

    public final NetworkType type;
    protected final ArrayList<TileEntity> contents = new ArrayList<>();

    public Network(NetworkType type) {
        this.type = type;
    }

    public static void join(TileEntity ent, ComponentNetwork cmp) {
        Set<Network> adjacentNetworks = new HashSet<>();
        for (TileEntity above : Utils.joinIterables(
                ent.getWorld().getTileEntities(ent.x, ent.y + 1), ent.getWorld().getTileEntities(ent.x, ent.y - 1),
                ent.getWorld().getTileEntities(ent.x - 1, ent.y), ent.getWorld().getTileEntities(ent.x + 1, ent.y))) {
            ComponentNetwork lcmp = above.getComponent(cmp.type.cls);
            if (lcmp != null && lcmp.getNetwork(above) != null) {
                adjacentNetworks.add(lcmp.getNetwork(above));
            }
        }
        if (adjacentNetworks.isEmpty()) {
            cmp.type.newNetwork().add(ent, cmp);
        } else { // Join adjacent networks, if any.
            Iterator<Network> itr = adjacentNetworks.iterator();
            Network addTo = itr.next();
            addTo.add(ent, cmp);
            while (itr.hasNext()) {
                addTo.joinFrom(itr.next());
            }
        }
        // TODO: could just recalculate in the second case...
        cmp.getNetwork(ent).recalculate();
    }

    public void add(TileEntity ent, ComponentNetwork cmp) {
        if (cmp.type != type) {
            throw new IllegalArgumentException();
        }
        if (cmp.getNetwork(ent) != null) {
            throw new IllegalArgumentException();
        }
        cmp.setNetwork(ent, this);
        contents.add(ent);
        recalculate();
    }

    public abstract void recalculate();

    public void joinFrom(Network next) {
        if (next.type != this.type) {
            throw new IllegalArgumentException();
        }
        contents.addAll(next.contents);
        next.contents.clear();
        for (TileEntity ent : contents) {
            ent.getComponent(type.cls).setNetwork(ent, this);
        }
        recalculate();
    }

    public void removeForSave(TileEntity ent, ComponentNetwork<?> cmp) {
        cmp.setNetwork(ent, null);
        contents.remove(ent);
        recalculate();
    }

    public void remove(TileEntity ent, ComponentNetwork<?> cmp) {
        cmp.setNetwork(ent, null);
        contents.remove(ent);
        rejoin();
    }

    protected void rejoin() {
        for (TileEntity e : contents) {
            ComponentNetwork cmp = e.getComponent(type.cls);
            if (cmp.getNetwork(e) != this) {
                throw new IllegalStateException("Network is: " + cmp.getNetwork(e) + " instead of " + this);
            }
            cmp.setNetwork(e, null);
        }
        for (TileEntity ent : contents) {
            join(ent, ent.getComponent(type.cls));
        }
        for (TileEntity ent : contents) {
            ent.getComponent(type.cls).getNetwork(ent).recalculate();
        }
    }

    public void checkInvariants() {
        for (TileEntity ent : contents) {
            if (ent.getComponent(type.cls).getNetwork(ent) != this) {
                throw new IllegalStateException("Network is: " + ent.getComponent(type.cls).getNetwork(ent) + " instead of " + this);
            }
        }
    }

    public abstract void printNetworkDescription(World w);
}
