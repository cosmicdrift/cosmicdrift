package io.github.cosmicdrift.cosmicdrift.items;

import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.entities.EntityPlayer;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

public class ItemPlating extends Item {

    public ItemPlating() {
        super("Plating");
    }

    @Override
    public String getIcon() {
        return "item_plating.png";
    }

    @Override
    public boolean use(World w, EntityPlayer p, int x, int y, double dist) {
        if (dist > Tile.TILE_SIZE * 3) {
            return false;
        }
        Tile t = w.getTile(x, y);
        if (t != null) {
            return false;
        }
        w.putTile(x, y, new Tile(0, 0, 0));
        return true;
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
