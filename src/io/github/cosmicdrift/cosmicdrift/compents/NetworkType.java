package io.github.cosmicdrift.cosmicdrift.compents;

import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.components.ComponentNetwork;
import io.github.cosmicdrift.cosmicdrift.components.ComponentNetworkData;
import io.github.cosmicdrift.cosmicdrift.components.ComponentNetworkFluid;
import io.github.cosmicdrift.cosmicdrift.components.ComponentNetworkPower;
import io.github.cosmicdrift.cosmicdrift.networks.Packet;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

public enum NetworkType {

    FLUID(FluidNetwork.class, ComponentNetworkFluid.class),
    POWER(PowerNetwork.class, ComponentNetworkPower.class),
    DATA(DataNetwork.class, ComponentNetworkData.class);

    public final Class<? extends Network> netClass;
    public final Class<? extends ComponentNetwork> cls;

    private NetworkType(Class<? extends Network> nc, Class<? extends ComponentNetwork> cls) {
        netClass = nc;
        this.cls = cls;
    }

    public static int requestPower(TileEntity ent, ComponentNetworkPower cmp, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException();
        }
        PowerNetwork pnet = cmp.getNetwork(ent);
        amount = Math.min(amount, pnet.available);
        pnet.available -= amount;
        return amount;
    }

    public static boolean requestPowerOrNothing(TileEntity ent, ComponentNetworkPower cmp, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException();
        }
        PowerNetwork pnet = cmp.getNetwork(ent);
        if (pnet.available < amount) {
            return false;
        }
        pnet.available -= amount;
        return true;
    }

    public static int supplyPower(TileEntity ent, ComponentNetworkPower cmp, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException();
        }
        PowerNetwork pnet = cmp.getNetwork(ent);
        int o = pnet.available + amount;
        pnet.available = Math.min(pnet.capacity, o);
        return o - pnet.available;
    }

    public static void exchangeAir(TileEntity ent, ComponentNetworkFluid cmp, Tile t, int x, int y) {
        // TODO: Limit amount of gas exchange per tick?
        // TODO: Enforce capacity!
        FluidNetwork fnet = cmp.getNetwork(ent);
        if (fnet == null || ent.getWorld().isSolid(x, y)) {
            return;
        }
        if (t == null) {
            fnet.o2 /= 2;
            fnet.n2 /= 2;
            fnet.co2 /= 2;
        } else {
            int To2 = fnet.o2 + t.o2;
            int Tco2 = fnet.co2 + t.co2;
            int Tn2 = fnet.n2 + t.n2;
            fnet.o2 = To2 / 2;
            fnet.co2 = Tco2 / 2;
            fnet.n2 = Tn2 / 2;
            t.o2 = To2 - fnet.o2;
            t.co2 = Tco2 - fnet.co2;
            t.n2 = Tn2 - fnet.n2;
        }
    }

    public static void insertFluid(TileEntity ent, ComponentNetworkFluid cmp, int o2, int co2, int n2) {
        FluidNetwork fnet = cmp.getNetwork(ent);
        if (fnet == null) {
            return;
        }
        fnet.o2 += o2;
        fnet.co2 += co2;
        fnet.n2 += n2;
    }

    public static void transmit(TileEntity ent, ComponentNetworkData cmp, Packet pkt) {
        DataNetwork n = cmp.getNetwork(ent);
        if (n != null) {
            for (TileEntity e2 : n.contents) {
                if (e2 != ent) {
                    e2.message(pkt);
                }
            }
        }
    }

    public static final class PowerNetwork extends Network {

        private int capacity, available;

        public PowerNetwork() {
            super(POWER);
        }

        @Override
        public void rejoin() {
            super.rejoin();
            for (TileEntity ent : contents) {
                ComponentNetworkPower cnf = ent.getComponent(ComponentNetworkPower.class);
                double frac = cnf.capacity / this.capacity;
                cnf.getNetwork(ent).available += this.available * frac;
            }
        }

        @Override
        public void recalculate() {
            capacity = 0;
            for (TileEntity ent : contents) {
                capacity += ent.getComponent(ComponentNetworkPower.class).capacity;
            }
            checkInvariants();
        }

        @Override
        public void printNetworkDescription(World w) {
            w.print("Power Network:");
            w.print("Power " + available + " / " + capacity);
        }

        @Override
        public void remove(TileEntity ent, ComponentNetwork cmpo) {
            ComponentNetworkPower cmp = (ComponentNetworkPower) cmpo;
            //Remove pipe's air content from network, and add it to the environment.
            double proportion = cmp.capacity / capacity;
            available -= available * proportion;
            super.remove(ent, cmp);
        }
    }

    public static final class FluidNetwork extends Network {

        private int capacity, o2, co2, n2;

        public FluidNetwork() {
            super(FLUID);
        }

        @Override
        public void rejoin() {
            super.rejoin();
            for (TileEntity ent : contents) { // TODO: Make sure that no extra air is lost.
                ComponentNetworkFluid cnf = ent.getComponent(ComponentNetworkFluid.class);
                double fraction = cnf.capacity / capacity;
                FluidNetwork net = (FluidNetwork) cnf.getNetwork(ent);
                net.o2 += o2 * fraction;
                net.co2 += co2 * fraction;
                net.n2 += n2 * fraction;
            }
        }

        @Override
        public void recalculate() {
            capacity = 0;
            for (TileEntity ent : contents) {
                ComponentNetworkFluid cnf = ent.getComponent(ComponentNetworkFluid.class);
                capacity += cnf.capacity;
                cnf.dumpPreserved(ent, this);
            }
            checkInvariants();
        }

        @Override
        public void printNetworkDescription(World w) {
            w.print("Fluids Network: ");
            /*int tanks = 0, pipes = 0, vents = 0;
             for (ComponentEntity ent : contents) {
             if (ent instanceof TEntTank) {
             tanks++;
             } else if (ent instanceof TEntVent) {
             vents++;
             } else {
             pipes++;
             }
             }
             w.print("#Pipes: " + pipes);
             w.print("#Tanks: " + tanks);
             w.print("#Vents: " + vents);*/ // TODO: Readd this functionality
            w.print("#Components: " + contents.size());
            w.print("Capacity: " + capacity);
            w.print("O2: " + o2);
            w.print("CO2: " + co2);
            w.print("N2: " + n2);
        }

        public int extractO2(int capacity) {
            double proportion = capacity / this.capacity;
            int take = (int) (o2 * proportion);
            o2 -= proportion;
            return take;
        }

        public int extractCO2(int capacity) {
            double proportion = capacity / this.capacity;
            int take = (int) (co2 * proportion);
            co2 -= proportion;
            return take;
        }

        public int extractN2(int capacity) {
            double proportion = capacity / this.capacity;
            int take = (int) (n2 * proportion);
            n2 -= proportion;
            return take;
        }

        @Override
        public void remove(TileEntity ent, ComponentNetwork cmpo) {
            ComponentNetworkFluid cmp = (ComponentNetworkFluid) cmpo;
            //Remove pipe's air content from network, and either add it to the environment or into the tank's own container.
            Tile t = ent.getWorld().getTile(ent.x, ent.y);
            double proportion = cmp.capacity / (double) this.capacity;
            int do2 = (int) (this.o2 * proportion), dco2 = (int) (this.co2 * proportion), dn2 = (int) (this.n2 * proportion);
            if (do2 != 0 || dco2 != 0 || dn2 != 0) {
                // TODO: Make sure that gas is conserved.
                if (cmp.preserveContents) {
                    cmp.receivePreserved(ent, do2, dco2, dn2);
                } else if (!ent.getWorld().isSolid(ent.x, ent.y)) {
                    t.o2 += do2;
                    t.co2 += dco2;
                    t.n2 += dn2;
                }
                this.o2 -= do2;
                this.co2 -= dco2;
                this.n2 -= dn2;
            }
            super.remove(ent, cmp);
        }

        public void injectAir(int o2, int n2, int co2) {
            this.o2 += o2;
            this.n2 += n2;
            this.co2 += co2;
        }
    }

    public static final class DataNetwork extends Network {

        public DataNetwork() {
            super(DATA);
        }

        @Override
        public void recalculate() {
            checkInvariants();
        }

        @Override
        public void printNetworkDescription(World w) {
            w.print("Data network - #Components: " + contents.size());
        }
    }

    public Network newNetwork() {
        try {
            return netClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}
