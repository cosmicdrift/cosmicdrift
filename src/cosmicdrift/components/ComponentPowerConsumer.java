package cosmicdrift.components;

import cosmicdrift.compents.TileEntity;

public class ComponentPowerConsumer extends Component {

    private final String control;
    private final int rate;

    public ComponentPowerConsumer(String control, int rate) {
        this.control = control;
        this.rate = rate;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[]{control, rate};
    }

    @Override
    public void onTick(TileEntity ent) {
        if (control == null) {
            ent.getComponent(ComponentNetworkPower.class).requestPower(ent, rate);
        } else if (ent.get(control)) {
            if (ent.getComponent(ComponentNetworkPower.class).requestPower(ent, rate) < rate) {
                ent.set(control, false);
            }
        }
    }
}
