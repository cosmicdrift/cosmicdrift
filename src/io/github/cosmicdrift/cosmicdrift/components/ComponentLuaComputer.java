/*
    Cosmic Drift is a computer game about building simulated space stations.
    Copyright (C) 2014-2015 Cel Skeggs.

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

import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;
import io.github.cosmicdrift.cosmicdrift.compents.NetworkType;
import io.github.cosmicdrift.cosmicdrift.computer.LuaComputer;
import io.github.cosmicdrift.cosmicdrift.networks.Packet;

import java.io.IOException;
import java.util.HashMap;

public class ComponentLuaComputer extends Component {

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[0];
    }

    public short getNetID(TileEntity ent) {
        return ent.<Short>get("cpu-netid");
    }

    @Override
    public void presave(TileEntity ent) {
        ent.<Integer>set("cpu-netid", ent.<Short>get("cpu-netid").intValue());
    }

    @Override
    public void postsave(TileEntity ent) {
        ent.<Short>set("cpu-netid", ent.<Number>get("cpu-netid").shortValue());
    }

    @Override
    public void initialize(TileEntity ent) {
        ent.set("cpu-netid", ComponentNetworkData.generateID(this));
        try {
            ent.set("cpu-disk", LuaComputer.newDisk());
        } catch (IOException ex) {
            throw new RuntimeException("Could not generate disk", ex);
        }
        ent.set("cpu-core", null);
        updateIcon(ent);
    }

    private void updateIcon(TileEntity ent) {
        ent.icon = ent.<LuaComputer>get("cpu-core") == null ? "terminal_off.png" : "terminal.png";
    }

    @Override
    public void onTick(TileEntity ent) {
        LuaComputer core = ent.<LuaComputer>get("cpu-core");
        if (ent.getComponent(ComponentNetworkPower.class).requestPower(ent, 5) < 5) {
            ent.set("cpu-core", null);
            updateIcon(ent);
            return;
        }
        if (core == null) {
            core = new LuaComputer(getNetID(ent), ent.<HashMap<String, String>>get("cpu-disk"));
            ent.set("cpu-core", core);
            updateIcon(ent);
        }
        core.cycle(300);
        if (!core.sending.isEmpty()) {
            NetworkType.transmit(ent, ent.getComponent(ComponentNetworkData.class), core.sending.removeFirst());
        }
    }

    public String[] getLines(TileEntity ent) {
        LuaComputer core = ent.get("cpu-core");
        return core == null ? new String[0] : core.getLines();
    }

    @Override
    public void onMessage(TileEntity ent, Packet pkt) {
        LuaComputer core = ent.get("cpu-core");
        if (core != null) {
            core.received.add(pkt);
        }
    }

    public void keyPress(TileEntity ent, char key) {
        LuaComputer core = ent.get("cpu-core");
        if (core != null && key >= 0 && key <= 127) { // only ASCII for these computers!
            core.keyPress((byte) key);
        }
    }

    public void printNetworkID(TileEntity ent) {
        ent.getWorld().print("Network ID: " + (getNetID(ent) & 0xffff));
    }
}
