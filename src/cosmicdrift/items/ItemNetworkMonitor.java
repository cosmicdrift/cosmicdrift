package cosmicdrift.items;

import cosmicdrift.World;
import cosmicdrift.components.Component;
import cosmicdrift.compents.TileEntity;
import cosmicdrift.components.ComponentNetwork;
import cosmicdrift.components.ComponentBinComputer;
import cosmicdrift.components.ComponentLuaComputer;
import cosmicdrift.entities.EntityPlayer;

public class ItemNetworkMonitor extends Item {

    public ItemNetworkMonitor() {
        super("Network Monitor");
    }

    @Override
    public String getIcon() {
        return "item_computer.png";
    }

    @Override
    public boolean use(World w, EntityPlayer p, int x, int y, double dist) {
        boolean found = false;
        for (TileEntity e : w.getTileEntities(x, y)) {
            for (Component cmp : e.type.components) {
                if (cmp instanceof ComponentNetwork) {
                    ((ComponentNetwork) cmp).printNetworkDescription((TileEntity) e, w);
                } else if (cmp instanceof ComponentBinComputer) {
                    ((ComponentBinComputer) cmp).printNetworkID(e);
                } else if (cmp instanceof ComponentLuaComputer) {
                    ((ComponentLuaComputer) cmp).printNetworkID(e);
                }
            }
            found = true;
        }
        if (!found) {
            w.print("No network detected.");
        }
        return false;
    }

    @Override
    protected boolean itemEqual(Item item) {
        return true;
    }

    @Override
    protected int itemHash() {
        return 0;
    }
}
