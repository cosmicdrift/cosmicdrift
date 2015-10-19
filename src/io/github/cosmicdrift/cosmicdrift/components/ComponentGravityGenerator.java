package io.github.cosmicdrift.cosmicdrift.components;

import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;
import io.github.cosmicdrift.cosmicdrift.Chunk;
import io.github.cosmicdrift.cosmicdrift.entities.Entity;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

public class ComponentGravityGenerator extends Component {

    public final int rWid = 8, lWid = 8, tHei = 8, bHei = 8;
    public final int gravityScale = 4;
    private final String enabledVar;

    public ComponentGravityGenerator(String enabledVar) {
        this.enabledVar = enabledVar;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[]{enabledVar};
    }

    @Override
    public void onTick(TileEntity ent) {
        ComponentNetworkPower power = ent.getComponent(ComponentNetworkPower.class);
        if (enabledVar != null && !ent.<Boolean>get(enabledVar)) {
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
                    e.vY += power.requestPower(ent, gravityScale);
                    // TODO: Figure out how to make this not break separation of concerns. Power shouldn't be handled here!
                }
            }
        }
    }
}
