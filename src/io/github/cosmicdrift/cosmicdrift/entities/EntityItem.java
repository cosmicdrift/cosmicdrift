/*
    Cosmic Drift is a computer game about building simulated space stations.
    Copyright (C) 2014-2015 Colby Skeggs and Christopher Quisling.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
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
