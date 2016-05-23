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
package io.github.cosmicdrift.cosmicdrift.items;

import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.entities.EntityPlayer;

import java.util.Objects;

public abstract class Item {

    public final String itemName;

    public Item(String itemName) {
        this.itemName = itemName;
    }

    public String getIcon() {
        return null;
    }

    public boolean use(World w, EntityPlayer p, int x, int y, double dist) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return (o.getClass() == getClass()) && ((Item) o).itemName.equals(itemName) && itemEqual((Item) o);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.itemName);
        hash = 41 * hash + itemHash();
        return hash;
    }

    protected abstract boolean itemEqual(Item item);

    protected abstract int itemHash();
}
