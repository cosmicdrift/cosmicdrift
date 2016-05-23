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

    // positive for consumption, negative for production.
    private final int watts;
    private final String enable;

    public ComponentNetworkPower() {
        this(0, null);
    }

    public ComponentNetworkPower(int watts, String enable) {
        super(NetworkType.POWER);
        this.watts = watts;
        this.enable = enable;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[]{watts, enable};
    }

    @Override
    public void initialize(TileEntity ent) {
        super.initialize(ent);
    }

    public int getAvailableWattage(TileEntity ent) {
        return enable != null && ent.<Boolean>get(enable) ? Math.max(0, -watts) : 0;
    }

    public int getRequestedWattage(TileEntity ent) {
        return enable != null && ent.<Boolean>get(enable) ? Math.max(0, watts) : 0;
    }

    @Override
    public void onVariableChange(TileEntity ent, String var, Object o) {
        if (enable != null && enable.equals(var)) {
            // TODO: will this always update the fraction?
            getNetwork(ent).recalculate();
        }
    }

    public void onPowerNetworkUpdate(TileEntity ent, double consumer_fraction, double producer_fraction) {
        boolean enabled = enable != null && ent.<Boolean>get(enable);
        ent.set("power_consumption_fraction", watts > 0 && enabled ? consumer_fraction : 0);
        ent.set("power_production_fraction", watts < 0 && enabled ? producer_fraction : 0);
    }
}
