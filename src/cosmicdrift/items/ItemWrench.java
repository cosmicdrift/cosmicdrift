package cosmicdrift.items;

import cosmicdrift.*;
import cosmicdrift.compents.TileEntity;
import cosmicdrift.entities.EntityPlayer;
import cosmicdrift.tiles.Tile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemWrench extends Item {

    private static final Logger logger = Logger.getLogger("cosmicdrift.items.ItemWrench");
    static {
        logger.setLevel(Level.ALL);
    }

    public ItemWrench() {
        super("Wrench");
    }

    @Override
    public String getIcon() {
        return "item_wrench.png";
    }

    @Override
    public boolean use(World w, EntityPlayer p, int x, int y, double dist) {
        if (dist > Tile.TILE_SIZE * 3) {
            return false;
        }
        logger.log(Level.FINE, "Start use of {0} on {1}, {2} by {3}", new Object[]{this, x, y, p});
        for (TileEntity e : w.getTileEntities(x, y)) {
            Item i = e.getAsItemForDrop();
            logger.log(Level.FINER, "Got item: {0}", i);
            if (i != null) {
                w.removeTileEntity(x, y, e);
                w.dropItemAtTile(x, y, i);
                return false;
            }
        }
        Tile old = w.getTile(x, y);
        if (old != null) {
            w.putTile(x, y, null);
            w.dropItemAtTile(x, y, new ItemPlating());
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
