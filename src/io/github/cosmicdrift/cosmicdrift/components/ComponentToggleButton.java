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
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ComponentToggleButton extends Component {

    private static final Logger logger = Logger.getLogger("ComponentToggleButton");

    private final String control;
    private final int minimum;
    private final int maximum;

    public ComponentToggleButton(String control, int minimum, int maximum) {
        this.control = control;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[] {control, minimum, maximum};
    }

    @Override
    public boolean onActivate(TileEntity ent, double dist) {
        if (dist >= minimum * Tile.TILE_SIZE && dist <= maximum * Tile.TILE_SIZE) {
            logger.log(Level.INFO, "Activated {0} at {1}", new Object[]{ent, dist});
            if (!ent.set(control, !ent.<Boolean>get(control))) {
                logger.info("Failed to activate");
            }
            return true;
        } else {
            logger.log(Level.INFO, "Failed to activate {0} at {1}", new Object[]{ent, dist});
            return false;
        }
    }
}
