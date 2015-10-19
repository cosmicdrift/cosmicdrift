package io.github.cosmicdrift.cosmicdrift.items;

import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.entities.EntityPlayer;

import java.util.Objects;

public abstract class Item {

    public final String itemName;

    public Item(String itemName) {
        this.itemName = itemName;
    }

    public String getIcon() {
        return null;
    }

    public boolean use(World w, EntityPlayer p, int x, int y, double dist) {
        return false;
    }
    
    @Override
    public boolean equals(Object o) {
        return (o.getClass() == getClass()) && ((Item) o).itemName.equals(itemName) && itemEqual((Item) o);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.itemName);
        hash = 41 * hash + itemHash();
        return hash;
    }

    protected abstract boolean itemEqual(Item item);
    
    protected abstract int itemHash();
}
