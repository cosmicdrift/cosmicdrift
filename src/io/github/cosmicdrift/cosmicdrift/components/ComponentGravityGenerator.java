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
import io.github.cosmicdrift.cosmicdrift.Chunk;
import io.github.cosmicdrift.cosmicdrift.entities.Entity;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

public class ComponentGravityGenerator extends Component {

    public final int rWid = 8, lWid = 8, tHei = 8, bHei = 8;
    public final int gravityScale = 4;
    private final String fractionalVar;

    public ComponentGravityGenerator(String fractionalVar) {
        this.fractionalVar = fractionalVar;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[]{fractionalVar};
    }

    @Override
    public void onTick(TileEntity ent) {
        double fraction = Math.max(0, Math.min(1, ent.<Double>get(fractionalVar)));
        if (fraction == 0) {
            return;
        }
        int mx = ent.x * Tile.TILE_SIZE;
        int my = ent.y * Tile.TILE_SIZE;
        int rightEnd = mx + (rWid + 1) * Tile.TILE_SIZE;
        int leftEnd = mx - lWid * Tile.TILE_SIZE;
        int bottomEnd = my + (bHei + 1) * Tile.TILE_SIZE;
        int topEnd = my - tHei * Tile.TILE_SIZE;
        for (Chunk c : ent.getWorld().getChunkList()) {
            for (Entity e : c.entities) {
                if (e.x2 <= rightEnd && e.x1 >= leftEnd && e.y2 <= bottomEnd && e.y1 >= topEnd) {
                    // TODO: Fix this so that this can go in reverse gravity.
                    // TODO: More energy for further away.
                    e.vY += gravityScale * fraction;
                }
            }
        }
    }
}
