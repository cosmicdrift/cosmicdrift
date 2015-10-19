package cosmicdrift.components;

import cosmicdrift.compents.Network;
import cosmicdrift.compents.NetworkType;
import cosmicdrift.compents.TileEntity;
import cosmicdrift.World;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ComponentNetwork<T extends Network> extends Component {

    private static final Logger logger = Logger.getLogger("cosmicdrift.components.ComponentNetwork");

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
        setNetwork(ent, null);
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
