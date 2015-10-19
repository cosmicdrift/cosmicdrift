package cosmicdrift.components;

import cosmicdrift.compents.TileEntity;
import cosmicdrift.compents.NetworkType;
import cosmicdrift.computer.BinComputerCore;
import cosmicdrift.computer.BinComputerData;
import cosmicdrift.networks.Packet;
import java.io.IOException;

public class ComponentBinComputer extends Component {

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
            ent.set("cpu-disk", BinComputerData.generateDisk());
        } catch (IOException ex) {
            throw new RuntimeException("Could not generate disk", ex);
        }
        ent.set("cpu-core", null);
        updateIcon(ent);
    }

    private void updateIcon(TileEntity ent) {
        ent.icon = ent.<BinComputerCore>get("cpu-core") == null ? "terminal_off.png" : "terminal.png";
    }

    @Override
    public void onTick(TileEntity ent) {
        BinComputerCore core = ent.<BinComputerCore>get("cpu-core");
        if (ent.getComponent(ComponentNetworkPower.class).requestPower(ent, 5) < 5) {
            ent.set("cpu-core", null);
            updateIcon(ent);
            return;
        }
        if (core == null) {
            core = new BinComputerCore(BinComputerData.bootstrap, ent.<byte[]>get("cpu-disk"));
            core.setNetworkAddress(getNetID(ent));
            ent.set("cpu-core", core);
            updateIcon(ent);
        }
        for (int i = 0; i < 256; i++) {
            if (core.cycle()) {
                break; // Pause!
            }
        }
        if (!core.sending.isEmpty()) {
            System.out.println("Transmit!");
            NetworkType.transmit(ent, ent.getComponent(ComponentNetworkData.class), core.sending.removeFirst());
        }
    }

    public String[] getLines(TileEntity ent) {
        BinComputerCore core = ent.get("cpu-core");
        return core == null ? new String[0] : core.screen;
    }

    @Override
    public void onMessage(TileEntity ent, Packet pkt) {
        BinComputerCore core = ent.get("cpu-core");
        if (core != null) {
            System.out.println("Receive!");
            core.received.add(pkt);
        } else {
            System.out.println("NO CORE!");
        }
    }

    public void keyPress(TileEntity ent, char key) {
        BinComputerCore core = ent.get("cpu-core");
        if (core != null && key >= 0 && key <= 127) { // only ASCII for these computers!
            core.keyPress((byte) key);
        }
    }

    public void printNetworkID(TileEntity ent) {
        ent.getWorld().print("Network ID: " + (getNetID(ent) & 0xffff));
    }
}
