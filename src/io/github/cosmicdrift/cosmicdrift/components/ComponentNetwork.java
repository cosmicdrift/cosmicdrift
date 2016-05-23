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
package io.github.cosmicdrift.cosmicdrift.components;

import io.github.cosmicdrift.cosmicdrift.compents.Network;
import io.github.cosmicdrift.cosmicdrift.compents.NetworkType;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;
import io.github.cosmicdrift.cosmicdrift.World;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ComponentNetwork<T extends Network> extends Component {

    private static final Logger logger = Logger.getLogger("ComponentNetwork");

    static {
        logger.setLevel(Level.FINER);
    }

    public final NetworkType type;

    public ComponentNetwork(NetworkType type) {
        this.type = type;
    }

    @Override
    public void initialize(TileEntity ent) {
        setNetwork(ent, null);
        Network.join(ent, this);
        if (getNetwork(ent) == null) {
            throw new NullPointerException();
        }
    }

    @Override
    public void presave(TileEntity ent) {
        getNetwork(ent).removeForSave(ent, this);
        if (getNetwork(ent) != null) {
            throw new IllegalStateException();
        }
    }

    @Override
    public void postsave(TileEntity ent) {
        Network.join(ent, this);
        if (getNetwork(ent) == null) {
            throw new NullPointerException();
        }
    }

    @Override
    public void onRemove(TileEntity ent) {
        getNetwork(ent).checkInvariants();
        getNetwork(ent).remove(ent, this);
    }

    @Override
    public void onAdded(TileEntity ent) {
        getNetwork(ent).recalculate();
    }

    public void printNetworkDescription(TileEntity ent, World w) {
        getNetwork(ent).printNetworkDescription(w);
    }

    public T getNetwork(TileEntity ent) {
        T net = ent.get("network-" + type.name());
        logger.log(Level.FINEST, "Fetched network for {0}, is {1}", new Object[]{ent, net});
        return net;
    }

    public void setNetwork(TileEntity ent, T network) {
        logger.log(Level.FINER, "Setting network for {0} to {1}", new Object[]{ent, network});
        ent.set("network-" + type.name(), network);
    }
}
