package io.github.cosmicdrift.cosmicdrift.components;

import io.github.cosmicdrift.cosmicdrift.compents.NetworkType;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;

public final class ComponentNetworkPower extends ComponentNetwork<NetworkType.PowerNetwork> {

    public final int capacity;

    public ComponentNetworkPower(int capacity) {
        super(NetworkType.POWER);
        if (capacity <= 0) {
            throw new IllegalArgumentException("Non-positive capacity!");
        }
        this.capacity = capacity;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[]{capacity};
    }

    public int supplyPower(TileEntity ent, int amount) {
        return NetworkType.supplyPower(ent, this, amount);
    }

    public int requestPower(TileEntity ent, int amount) {
        return NetworkType.requestPower(ent, this, amount);
    }

    public boolean requestPowerOrNothing(TileEntity ent, int amount) {
        return NetworkType.requestPowerOrNothing(ent, this, amount);
    }
}
