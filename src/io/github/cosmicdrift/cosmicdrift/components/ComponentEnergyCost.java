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

public class ComponentEnergyCost extends Component {

    private final String enableVar;
    private final int cost;

    public ComponentEnergyCost(String enableVar, int cost) {
        this.enableVar = enableVar;
        this.cost = cost;
        if (cost <= 0) {
            throw new IllegalArgumentException("Nonpositive energy cost!");
        }
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[]{enableVar, cost};
    }

    @Override
    public boolean beforeVariableChange(TileEntity ent, String var, Object o) {
        if (!var.equals(enableVar)) {
            return false;
        }
        boolean b = (Boolean) o;
        Boolean old = ent.<Boolean>get(enableVar);
        if (old == null || b == old) {
            return false;
        }
        // Deny variable change if there isn't enough power.
        return !ent.getComponent(ComponentNetworkPower.class).requestPowerOrNothing(ent, cost);
    }
}
