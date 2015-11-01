/*
    Cosmic Drift is a computer game about building simulated space stations.
    Copyright (C) 2014-2015 Colby Skeggs.

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

import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.compents.NetworkType;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;
import io.github.cosmicdrift.cosmicdrift.networks.Packet;

public class ComponentNetworkDataEndpoint extends ComponentNetworkData {

    private final String enableVar;

    public ComponentNetworkDataEndpoint(String enableVar) {
        this.enableVar = enableVar;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[]{enableVar};
    }

    public short getNetID(TileEntity ent) {
        return ent.<Short>get("data-netid");
    }
    
    @Override
    public void presave(TileEntity ent) {
        super.presave(ent);
        ent.<Integer>set("data-netid", ent.<Short>get("data-netid").intValue());
    }
    
    @Override
    public void postsave(TileEntity ent) {
        super.postsave(ent);
        ent.<Short>set("data-netid", ent.<Number>get("data-netid").shortValue());
    }

    @Override
    public void initialize(TileEntity ent) {
        super.initialize(ent);
        ent.set("data-netid", ComponentNetworkData.generateID(ent));
    }

    @Override
    public void printNetworkDescription(TileEntity ent, World w) {
        w.print("NetID: " + (getNetID(ent) & 0xffff));
    }

    @Override
    public void onMessage(TileEntity ent, Packet pkt) {
        if (enableVar == null) {
            switch (ComponentNetworkData.decodeMessage(pkt, ent.<Short>get("data-netid"), "ping")) {
                case 0:
                    NetworkType.transmit(ent, this, new Packet(("pong-" + ent.type.typename + "-" + (getNetID(ent) & 0xffff)).getBytes(), getNetID(ent), pkt.sourceID));
                    break;
            }
        } else {
            boolean state = ent.get(enableVar);
            switch (ComponentNetworkData.decodeMessage(pkt, getNetID(ent), "enable", "disable", "toggle", "ping")) {
                case 0:
                    ent.set(enableVar, true);
                    break;
                case 1:
                    ent.set(enableVar, false);
                    break;
                case 2:
                    ent.set(enableVar, !state);
                    break;
                case 3:
                    NetworkType.transmit(ent, this, new Packet(("pong-" + ent.type.typename + "-" + (getNetID(ent) & 0xffff) + "-" + (state ? "enabled" : "disabled")).getBytes(), getNetID(ent), pkt.sourceID));
                    break;
            }
        }
    }
}
