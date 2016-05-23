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

import io.github.cosmicdrift.cosmicdrift.compents.NetworkType;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;

public final class ComponentNetworkPower extends ComponentNetwork<NetworkType.PowerNetwork> {

    public final int capacity;

    public ComponentNetworkPower(int capacity) {
        super(NetworkType.POWER);
        if (capacity <= 0) {
            throw new IllegalArgumentException("Non-positive capacity!");
        }
        this.capacity = capacity;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[]{capacity};
    }

    public int supplyPower(TileEntity ent, int amount) {
        return NetworkType.supplyPower(ent, this, amount);
    }

    public int requestPower(TileEntity ent, int amount) {
        return NetworkType.requestPower(ent, this, amount);
    }

    public boolean requestPowerOrNothing(TileEntity ent, int amount) {
        return NetworkType.requestPowerOrNothing(ent, this, amount);
    }

    @Override
    public void initialize(TileEntity ent) {
        if (ent.<Integer>get("preservePower") == null) {
            ent.set("preservePower", 0);
        }
        super.initialize(ent);
    }

    public void receivePreserved(TileEntity ent, int power) {
        if (ent.<Number>get("preservePower").intValue() != 0) {
            throw new RuntimeException("Add power to already-preserved node!");
        }
        ent.<Integer>set("preservePower", power);
    }

    public int dumpPreserved(TileEntity ent) {
        int out = ent.<Number>get("preservePower").intValue();
        ent.<Integer>set("preservePower", 0);
        return out;
    }
}
