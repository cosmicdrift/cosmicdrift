package cosmicdrift;

import cosmicdrift.items.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Inventory {

    public Item leftHand = null, rightHand = null;
    public final int slotCount;
    public final ArrayList<Item> inv = new ArrayList<>();
    public static final int DEFAULT_INVSIZE = 32;
    
    public static Inventory defaultInventory(World w) {
        Inventory out = new Inventory(DEFAULT_INVSIZE);
        for (int i = 0; i < 5; i++) {
            out.deposit(w.getTileEntityType("fluid-pipe").getAsItemForDrop());
        }
        for (int i = 0; i < 5; i++) {
            out.deposit(w.getTileEntityType("conductive-wire").getAsItemForDrop());
        }
        for (int i = 0; i < 5; i++) {
            out.deposit(w.getTileEntityType("data-wire").getAsItemForDrop());
        }
        out.deposit(w.getTileEntityType("tank").getAsItemForDrop());
        out.deposit(w.getTileEntityType("vent").getAsItemForDrop());
        out.deposit(new ItemWrench());
        out.deposit(new ItemNetworkMonitor());
        out.deposit(new ItemFluidsMonitor());
        return out;
    }

    public Inventory(int slotCount) {
        this.slotCount = slotCount;
    }

    public boolean deposit(Item item) {
        if ((inv.size() + 1) <= slotCount) {
            inv.add(item);
            return true;
        } else {
            return false;
        }
    }

    public void addAllFromLoad(List<Item> contents) throws IOException {
        if (inv.size() + contents.size() > slotCount) {
            throw new IOException("Too many items in saved inventory!");
        }
        inv.addAll(contents);
    }
}
