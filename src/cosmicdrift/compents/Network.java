package cosmicdrift.compents;

import cosmicdrift.Utils;
import cosmicdrift.World;
import cosmicdrift.components.ComponentNetwork;
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
            if (above instanceof TileEntity) {
                ComponentNetwork lcmp = ((TileEntity) above).getComponent(cmp.type.cls);
                if (lcmp != null && lcmp.getNetwork((TileEntity) above) != null) {
                    adjacentNetworks.add(lcmp.getNetwork((TileEntity) above));
                }
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

    public void remove(TileEntity ent, ComponentNetwork cmp) {
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
