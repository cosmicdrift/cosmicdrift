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
package io.github.cosmicdrift.cosmicdrift;

import io.github.cosmicdrift.cosmicdrift.compents.TileEntityType;
import io.github.cosmicdrift.cosmicdrift.items.Item;
import io.github.cosmicdrift.cosmicdrift.items.ItemFluidsMonitor;
import io.github.cosmicdrift.cosmicdrift.items.ItemNetworkMonitor;
import io.github.cosmicdrift.cosmicdrift.items.ItemWrench;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Inventory {

    public Item leftHand = null, rightHand = null;
    public final int slotCount;
    public final ArrayList<Item> inv = new ArrayList<>();
    public static final int DEFAULT_INVSIZE = 32;
    
    public static Inventory defaultInventory(World w) {
        Inventory out = new Inventory(DEFAULT_INVSIZE);
        for (int i = 0; i < 5; i++) {
            out.deposit(TileEntityType.forName("fluid-pipe").getAsItemForDrop());
        }
        for (int i = 0; i < 5; i++) {
            out.deposit(TileEntityType.forName("conductive-wire").getAsItemForDrop());
        }
        for (int i = 0; i < 5; i++) {
            out.deposit(TileEntityType.forName("data-wire").getAsItemForDrop());
        }
        out.deposit(TileEntityType.forName("tank").getAsItemForDrop());
        out.deposit(TileEntityType.forName("vent").getAsItemForDrop());
        out.deposit(new ItemWrench());
        out.deposit(new ItemNetworkMonitor());
        out.deposit(new ItemFluidsMonitor());
        return out;
    }

    public Inventory(int slotCount) {
        this.slotCount = slotCount;
    }

    public boolean deposit(Item item) {
        if ((inv.size() + 1) <= slotCount) {
            inv.add(item);
            return true;
        } else {
            return false;
        }
    }

    public void addAllFromLoad(List<Item> contents) throws IOException {
        if (inv.size() + contents.size() > slotCount) {
            throw new IOException("Too many items in saved inventory!");
        }
        inv.addAll(contents);
    }
}
