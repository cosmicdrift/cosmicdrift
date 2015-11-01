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
package io.github.cosmicdrift.cosmicdrift.entities;

import io.github.cosmicdrift.cosmicdrift.Inventory;
import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

public class EntityPlayer extends Entity {

    public static final int MAX_HP = 100;

    public int HP = MAX_HP; //Percent.
    public int oxygen = 150; //Out of 200.
    public final Inventory inv;
    boolean facingLeft = false;
    public boolean isInItemPickupMode = false;

    public EntityPlayer(int startX, int startY, World world, Inventory inv) {
        super(startX, startY, 24, 50, world);
        this.inv = inv;
    }


    public void breathe(Tile tile) {
        if (oxygen <= 20 && HP > 0) {
            HP--;
        }
        if (oxygen > 0) {
            oxygen--;
        }
        if (tile != null) {
            int totalGas = tile.co2 + tile.n2 + tile.o2;
            if (oxygen < 150 && totalGas > 0) {
                int breathsize = 10 + (200 - oxygen) / 23;
                breathsize *= totalGas / 1000f;
                int breath = tile.o2 * breathsize / totalGas;
                oxygen += breath;
                tile.o2 -= breath;
                tile.co2 += breath;
            }
        }
    }

    @Override
    public boolean tick() {
        super.tick();
        if (vX > 0) {
            facingLeft = false;
        } else if (vX < 0) {
            facingLeft = true;
        }
        breathe(world.getTile((x1 + x2) / 2 / Tile.TILE_SIZE, (y1 + y2) / 2 / Tile.TILE_SIZE));
        return false;
    }

    public boolean isDead() {
        return HP <= 0;
    }

    @Override
    public String getIcon() {
        return facingLeft ? "player_left.png" : "player_right.png";
    }
}
