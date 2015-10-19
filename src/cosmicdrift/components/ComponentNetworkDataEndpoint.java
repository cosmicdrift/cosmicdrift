package cosmicdrift.components;

import cosmicdrift.World;
import cosmicdrift.compents.NetworkType;
import cosmicdrift.compents.TileEntity;
import cosmicdrift.networks.Packet;

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
        ent.<Short>set("data-netid", ent.<Integer>get("data-netid").shortValue());
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
