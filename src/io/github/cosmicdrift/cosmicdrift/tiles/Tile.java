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
package io.github.cosmicdrift.cosmicdrift.tiles;

import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;
import java.util.Random;

public class Tile implements Cloneable {

    public static final int TILE_SIZE = 25;

    public int o2, n2, co2;
    private static final Random r = new Random();

    public Tile(int o2, int co2, int n2) {
        this.o2 = o2;
        this.co2 = co2;
        this.n2 = n2;
    }

    public Tile(Tile tile) {
        this(tile.o2, tile.co2, tile.n2);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tile)) {
            return false;
        }
        Tile t = (Tile) o;
        return o2 == t.o2 && n2 == t.n2 && co2 == t.co2;
    }

    @Override
    public int hashCode() {
        return (o2 * 3) ^ (n2 * 5) ^ (co2 * 7);
    }

    public void tick(World world, int bx, int by) {
        if (world.isSolid(bx, by)) {
            expell(world, bx, by);
            return;
        }
        if (r.nextBoolean()) {
            return;
        }
        int[] base = new int[]{0, 1, 2, 3};
        int[] rels = new int[4];
        for (int i = 0; i < 4; i++) {
            int c = -1;
            while (c == -1) {
                c = base[r.nextInt(4)];
            }
            rels[i] = c;
            base[c] = -1;
        }
        for (int k : rels) {
            switch (k) {
                case 0:
                    exchange(world, bx, by + 1);
                    break;
                case 1:
                    exchange(world, bx, by - 1);
                    break;
                case 2:
                    exchange(world, bx + 1, by);
                    break;
                case 3:
                    exchange(world, bx - 1, by);
                    break;
            }
        }
    }

    private void exchange(World world, int x, int y) {
        Tile t = world.getTile(x, y);
        if (t == null) {
            o2 /= 2;
            n2 /= 2;
            co2 /= 2;
        } else if (!world.isSolid(x, y)) {
            int To2 = o2 + t.o2;
            int Tco2 = co2 + t.co2;
            int Tn2 = n2 + t.n2;
            o2 = To2 / 2;
            co2 = Tco2 / 2;
            n2 = Tn2 / 2;
            t.o2 = To2 - o2;
            t.co2 = Tco2 - co2;
            t.n2 = Tn2 - n2;
        }
    }

    public void expell(World world, int bx, int by) {
        int[] base = new int[]{0, 1, 2, 3};
        int[] rels = new int[4];
        for (int i = 0; i < 4; i++) {
            int c = -1;
            while (c == -1) {
                c = base[r.nextInt(4)];
            }
            rels[i] = c;
            base[c] = -1;
        }
        for (int k : rels) {
            boolean rx = (k & 1) == 0;
            boolean ry = (k & 2) == 0;
            expellTo(world, rx ? bx + 1 : bx - 1, ry ? by + 1 : by - 1);
        }
    }

    private void expellTo(World world, int x, int y) {
        Tile t = world.getTile(x, y);
        if (t == null) {
            o2 = co2 = n2 = 0;
        } else if (!world.isSolid(x, y)) {
            t.o2 += o2;
            o2 = 0;
            t.co2 += co2;
            co2 = 0;
            t.n2 += n2;
            n2 = 0;
        }
    }

    public static void activate(World w, Tile t, int x, int y, double dist) {
        for (TileEntity ent : w.getTileEntities(x, y)) {
            if (ent.activate(dist)) {
                return;
            }
        }
    }

    public Tile duplicate() {
        return new Tile(o2, co2, n2);
    }

    public static Tile duplicate(Tile flat) {
        return flat == null ? null : flat.duplicate();
    }
}
