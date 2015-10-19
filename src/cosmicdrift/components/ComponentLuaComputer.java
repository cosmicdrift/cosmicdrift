package cosmicdrift.components;

import cosmicdrift.compents.TileEntity;
import cosmicdrift.compents.NetworkType;
import cosmicdrift.computer.LuaComputer;
import cosmicdrift.networks.Packet;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        ent.<Short>set("cpu-netid", ent.<Integer>get("cpu-netid").shortValue());
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
