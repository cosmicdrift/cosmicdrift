package io.github.cosmicdrift.cosmicdrift.components;

import io.github.cosmicdrift.cosmicdrift.compents.NetworkType;
import io.github.cosmicdrift.cosmicdrift.compents.NetworkType.FluidNetwork;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;

public final class ComponentNetworkFluid extends ComponentNetwork<NetworkType.FluidNetwork> {

    public final int capacity;
    public final boolean preserveContents;
    
    public ComponentNetworkFluid(int capacity) {
        this(capacity, false);
    }

    public ComponentNetworkFluid(int capacity, boolean preserveContents) {
        super(NetworkType.FLUID);
        this.capacity = capacity;
        if (capacity <= 0) {
            throw new IllegalArgumentException("Non-positive capacity!");
        }
        this.preserveContents = preserveContents;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[] {capacity, preserveContents};
    }
    
    @Override
    public void initialize(TileEntity ent) {
        if (ent.<Integer>get("preserveO2") == null) {
            ent.set("preserveO2", 0);
        }
        if (ent.<Integer>get("preserveCO2") == null) {
            ent.set("preserveCO2", 0);
        }
        if (ent.<Integer>get("preserveN2") == null) {
            ent.set("preserveN2", 0);
        }
        super.initialize(ent);
    }

    public void receivePreserved(TileEntity ent, int o2, int co2, int n2) {
        if (ent.<Integer>get("preserveO2") != 0 || ent.<Integer>get("preserveN2") != 0 || ent.<Integer>get("preserveCO2") != 0) {
            throw new RuntimeException("Add gas to already-preserved tank!");
        }
        ent.set("preserveCO2", co2);
        ent.set("preserveN2", n2);
        ent.set("preserveO2", o2);
    }

    public void dumpPreserved(TileEntity ent, FluidNetwork target) {
        target.injectAir(ent.<Integer>get("preserveO2"), ent.<Integer>get("preserveN2"), ent.<Integer>get("preserveCO2"));
        ent.set("preserveCO2", 0);
        ent.set("preserveN2", 0);
        ent.set("preserveO2", 0);
    }
}
