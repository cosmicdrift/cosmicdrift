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
import io.github.cosmicdrift.cosmicdrift.components.Component;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;
import io.github.cosmicdrift.cosmicdrift.components.ComponentNetwork;
import io.github.cosmicdrift.cosmicdrift.components.ComponentLuaComputer;
import io.github.cosmicdrift.cosmicdrift.entities.EntityPlayer;

public class ItemNetworkMonitor extends Item {

    public ItemNetworkMonitor() {
        super("Network Monitor");
    }

    @Override
    public String getIcon() {
        return "item_computer.png";
    }

    @Override
    public boolean use(World w, EntityPlayer p, int x, int y, double dist) {
        boolean found = false;
        for (TileEntity e : w.getTileEntities(x, y)) {
            for (Component cmp : e.type.components) {
                if (cmp instanceof ComponentNetwork) {
                    ((ComponentNetwork) cmp).printNetworkDescription((TileEntity) e, w);
                } else if (cmp instanceof ComponentLuaComputer) {
                    ((ComponentLuaComputer) cmp).printNetworkID(e);
                }
            }
            found = true;
        }
        if (!found) {
            w.print("No network detected.");
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
