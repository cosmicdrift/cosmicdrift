/*
    Cosmic Drift is a computer game about building simulated space stations.
    Copyright (C) 2014-2015 Cel Skeggs.

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
import io.github.cosmicdrift.cosmicdrift.entities.EntityPlayer;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

public class ComponentMedkit extends Component {

    private final String supplyVariable;

    public ComponentMedkit(String supplyVariable) {
        this.supplyVariable = supplyVariable;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[] {supplyVariable};
    }

    @Override
    public boolean onActivate(TileEntity ent, double dist) {
        final int supply = ent.get(supplyVariable);
        if (dist < Tile.TILE_SIZE * 3 && ent.getWorld().ply.HP < EntityPlayer.MAX_HP && supply > 0) {
            int lost = EntityPlayer.MAX_HP - ent.getWorld().ply.HP;
            if (lost > supply) {
                ent.getWorld().ply.HP += supply;
                ent.set(supplyVariable, 0);
            } else {
                ent.getWorld().ply.HP = EntityPlayer.MAX_HP;
                ent.set(supplyVariable, supply - lost);
            }
            return true;
        }
        return false;
    }
}
