/*
    Cosmic Drift is a computer game about building simulated space stations.
    Copyright (C) 2014-2015 Cel Skeggs and Christopher Quisling.

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
package io.github.cosmicdrift.cosmicdrift.components;

import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;
import io.github.cosmicdrift.cosmicdrift.dataio.PresetRef;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

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
        ent.getWorld().ply.inv.deposit(item.get().getAsItemForDrop());
        return true;
    }
}
