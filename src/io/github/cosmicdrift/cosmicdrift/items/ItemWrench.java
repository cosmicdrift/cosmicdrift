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
package io.github.cosmicdrift.cosmicdrift.items;

import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;
import io.github.cosmicdrift.cosmicdrift.entities.EntityPlayer;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemWrench extends Item {

    private static final Logger logger = Logger.getLogger("ItemWrench");
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
