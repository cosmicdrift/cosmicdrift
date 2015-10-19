package io.github.cosmicdrift.cosmicdrift.components;

import io.github.cosmicdrift.cosmicdrift.compents.NetworkType;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;

public class ComponentAirExchanger extends Component {

    @Override
    public void onTick(TileEntity ent) {
        NetworkType.exchangeAir(ent, ent.getComponent(ComponentNetworkFluid.class), ent.getWorld().getTile(ent.x, ent.y), ent.x, ent.y);
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[0];
    }
}
