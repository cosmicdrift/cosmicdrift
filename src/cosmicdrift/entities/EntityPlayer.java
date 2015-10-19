package cosmicdrift.entities;

import cosmicdrift.Inventory;
import cosmicdrift.World;
import cosmicdrift.tiles.Tile;

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
