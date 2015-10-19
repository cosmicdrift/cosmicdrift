package cosmicdrift.components;

import cosmicdrift.compents.TileEntity;

public class ComponentPowerGenerator extends Component {

    private final String control;
    private final int rate;

    public ComponentPowerGenerator(String control, int rate) {
        this.control = control;
        this.rate = rate;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[]{control, rate};
    }

    @Override
    public void onTick(TileEntity ent) {
        if (control == null || ent.<Boolean>get(control)) {
            ent.getComponent(ComponentNetworkPower.class).supplyPower(ent, rate);
        }
    }
}
