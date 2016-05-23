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

public class ComponentActivationSynchronizer extends Component {

    private final String variable;

    public ComponentActivationSynchronizer(String variable) {
        this.variable = variable;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[] {variable};
    }

    @Override
    public void onVariableChange(TileEntity ent, String var, Object value) {
        if (variable.equals(var)) {
            final boolean state = (boolean) value;
            int[][] ts = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] c : ts) {
                for (TileEntity t : ent.getWorld().getTileEntities(ent.x + c[0], ent.y + c[1])) {
                    if (t.type.typename.equals(ent.type.typename)) { // TODO: Is this how synchronization should be choosen?
                        if (state != t.<Boolean>get(variable)) {
                            t.set(variable, state);
                        }
                    }
                }
            }
        }
    }
}
