package io.github.cosmicdrift.cosmicdrift.entities;

import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.items.Item;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

public class EntityItem extends Entity { // TODO: Small versions of items for the map.

    public final Item item;

    public EntityItem(int x, int y, Item i, World world) {
        super(x, y, 10, 10, world);
        if (i == null) {
            throw new NullPointerException();
        }
        this.item = i;
    }

    @Override
    public boolean tick() {
        super.tick();
        int px = (world.ply.x1 + world.ply.x2) / 2;
        int py = (world.ply.y1 + world.ply.y2) / 2;
        int ex = (x1 + x2) / 2;
        int ey = (y1 + y2) / 2;
        int dx = px - ex;
        int dy = py - ey;
        if (dx * dx + dy * dy < Tile.TILE_SIZE * Tile.TILE_SIZE) {
            if (world.ply.isInItemPickupMode) {
                if (world.ply.inv.leftHand == null) {
                    world.ply.inv.leftHand = item;
                    return true;
                } else if (world.ply.inv.rightHand == null) {
                    world.ply.inv.rightHand = item;
                    return true;
                }
            }
        }
        return false;
    }
}
