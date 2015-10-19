package io.github.cosmicdrift.cosmicdrift.components;

import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;

public class ComponentEnergyCost extends Component {

    private final String enableVar;
    private final int cost;

    public ComponentEnergyCost(String enableVar, int cost) {
        this.enableVar = enableVar;
        this.cost = cost;
        if (cost <= 0) {
            throw new IllegalArgumentException("Nonpositive energy cost!");
        }
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[]{enableVar, cost};
    }

    @Override
    public boolean beforeVariableChange(TileEntity ent, String var, Object o) {
        if (!var.equals(enableVar)) {
            return false;
        }
        boolean b = (Boolean) o;
        Boolean old = ent.<Boolean>get(enableVar);
        if (old == null || b == old) {
            return false;
        }
        // Deny variable change if there isn't enough power.
        return !ent.getComponent(ComponentNetworkPower.class).requestPowerOrNothing(ent, cost);
    }
}
