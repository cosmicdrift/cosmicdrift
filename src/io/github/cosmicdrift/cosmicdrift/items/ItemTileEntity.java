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
import io.github.cosmicdrift.cosmicdrift.compents.TileEntityType;
import io.github.cosmicdrift.cosmicdrift.entities.EntityPlayer;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

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
        TileEntity out = new TileEntity(w, type, x, y, vars);
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
