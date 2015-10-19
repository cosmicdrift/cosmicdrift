package io.github.cosmicdrift.cosmicdrift.items;

import io.github.cosmicdrift.cosmicdrift.Chunk;
import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.entities.EntityPlayer;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

public class ItemFluidsMonitor extends Item {

    public ItemFluidsMonitor() {
        super("Fluids Monitor");
    }

    @Override
    public String getIcon() {
        return "item_window.png"; // TODO: Needs better icon
    }

    //Determine fluid content of a single tile.
    @Override
    public boolean use(World w, EntityPlayer p, int x, int y, double dist) {
        Tile t = w.getTile(x, y);
        if (t != null) {
            if (t.co2 == 0 && t.n2 == 0 && t.o2 == 0) {
                w.print("Solid/Empty - No fluid content.");
            } else {
                w.print("Fluids at (" + x + ", " + y + "): ");
                w.print(" O2: " + t.o2);
                w.print("CO2: " + t.co2);
                w.print(" N2: " + t.n2);
            }
        } else {
            w.print("Solid/Empty - No fluid content.");
        }
        return false;
    }

    //Determine total fluid contained in the current chunk.
    public boolean use2(World w, EntityPlayer p, int x, int y, double dist) {
        int total_o2 = 0;
        int total_co2 = 0;
        int total_n2 = 0;
        for (Chunk c : w.getChunkList()) {
            for (int i = 0; i < 64; i++) {
                for (int j = 0; j < 64; j++) {
                    Tile t = c.contents[i][j];
                    if (t != null) {
                        total_o2 += t.o2;
                        total_co2 += t.co2;
                        total_n2 += t.n2;
                    }
                }
            }
        }
        w.print("Total chunk fluid content: ");
        w.print(" O2: " + total_o2);
        w.print("CO2: " + total_co2);
        w.print(" N2: " + total_n2);
        w.print("\t");
        return false;
    }

    @Override
    protected boolean itemEqual(Item item) {
        return true;
    }

    @Override
    protected int itemHash() {
        return 0;
    }
}
