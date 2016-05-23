package io.github.cosmicdrift.cosmicdrift.components;

import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;

public class ComponentPowerMinimum extends Component {

    private final String enable;
    private final double minimum;

    public ComponentPowerMinimum(String enable, double minimum) {
        this.enable = enable;
        this.minimum = minimum;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[] { enable, minimum };
    }

    @Override
    public void onTick(TileEntity ent) {
        if (ent.<Boolean>get(enable) && ent.<Double>get("power_consumption_fraction") < minimum) {
            ent.set(enable, false);
        }
    }
}
