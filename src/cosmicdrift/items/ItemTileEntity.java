package cosmicdrift.items;

import cosmicdrift.World;
import cosmicdrift.compents.TileEntity;
import cosmicdrift.compents.TileEntityType;
import cosmicdrift.entities.EntityPlayer;
import cosmicdrift.tiles.Tile;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class ItemTileEntity extends Item {

    public final String icon;
    public final TileEntityType type;
    private final Map<String, Object> vars; // Immutable!

    public ItemTileEntity(TileEntityType type, String icon, Map<String, Object> vars) {
        super("item-" + type.typename);
        this.type = type;
        String ficon = (String) vars.get("item-icon");
        if (ficon == null) {
            ficon = icon;
            if (icon == null) {
                throw new NullPointerException();
            }
        }
        this.icon = ficon;
        this.vars = vars;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public boolean use(World w, EntityPlayer p, int x, int y, double dist) {
        if (dist > Tile.TILE_SIZE * 3) {
            return false;
        }
        Boolean lsolid = (Boolean) vars.get("solid");
        if (w.isSolid(x, y) && lsolid != null && lsolid) {
            return false; // Don't place a second solid object.
        }
        TileEntity out = new TileEntity(type, x, y, vars);
        w.addTileEntity(out);
        return true;
    }

    public Map<String, Object> getVars() {
        return Collections.unmodifiableMap(vars);
    }

    @Override
    protected boolean itemEqual(Item item) {
        ItemTileEntity other = (ItemTileEntity) item;
        return icon.equals(other.icon) && type.equals(other.type) && vars.equals(other.vars);
    }

    @Override
    public int itemHash() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.icon);
        hash = 97 * hash + Objects.hashCode(this.type);
        hash = 97 * hash + Objects.hashCode(this.vars);
        return hash;
    }
}
