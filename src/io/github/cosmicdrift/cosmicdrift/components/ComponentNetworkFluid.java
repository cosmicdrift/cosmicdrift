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
import io.github.cosmicdrift.cosmicdrift.compents.NetworkType.FluidNetwork;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;

public final class ComponentNetworkFluid extends ComponentNetwork<NetworkType.FluidNetwork> {

    public final int capacity;
    public final boolean preserveContents;

    public ComponentNetworkFluid(int capacity) {
        this(capacity, false);
    }

    public ComponentNetworkFluid(int capacity, boolean preserveContents) {
        super(NetworkType.FLUID);
        this.capacity = capacity;
        if (capacity <= 0) {
            throw new IllegalArgumentException("Non-positive capacity!");
        }
        this.preserveContents = preserveContents;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[]{capacity, preserveContents};
    }

    @Override
    public void initialize(TileEntity ent) {
        if (ent.<Integer>get("preserveO2") == null) {
            ent.set("preserveO2", 0);
        }
        if (ent.<Integer>get("preserveCO2") == null) {
            ent.set("preserveCO2", 0);
        }
        if (ent.<Integer>get("preserveN2") == null) {
            ent.set("preserveN2", 0);
        }
        super.initialize(ent);
    }

    public void receivePreserved(TileEntity ent, int o2, int co2, int n2) {
        if (ent.<Integer>get("preserveO2") != 0 || ent.<Integer>get("preserveN2") != 0 || ent.<Integer>get("preserveCO2") != 0) {
            throw new RuntimeException("Add gas to already-preserved node!");
        }
        ent.set("preserveCO2", co2);
        ent.set("preserveN2", n2);
        ent.set("preserveO2", o2);
    }

    public void dumpPreserved(TileEntity ent, FluidNetwork target) {
        target.injectAir(ent.<Integer>get("preserveO2"), ent.<Integer>get("preserveN2"), ent.<Integer>get("preserveCO2"));
        ent.set("preserveCO2", 0);
        ent.set("preserveN2", 0);
        ent.set("preserveO2", 0);
    }
}
