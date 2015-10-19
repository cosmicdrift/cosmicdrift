package cosmicdrift.components;

import cosmicdrift.compents.TileEntity;
import cosmicdrift.dataio.PresetRef;
import cosmicdrift.tiles.Tile;

public class ComponentDispenser extends Component {

    private final String message;
    private final PresetRef item;

    public ComponentDispenser(String message, PresetRef item) {
        this.message = message;
        this.item = item;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[]{message, item};
    }

    @Override
    public boolean onActivate(TileEntity ent, double dist) {
        ent.getWorld().print(message);
        if (dist > Tile.TILE_SIZE * 3) {
            return false;
        }
        ent.getWorld().ply.inv.deposit(item.get(ent.getWorld()).getAsItemForDrop());
        return true;
    }
}
