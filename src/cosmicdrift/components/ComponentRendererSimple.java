package cosmicdrift.components;

import cosmicdrift.compents.TileEntity;

public class ComponentRendererSimple extends Component {

    private final String icon;

    public ComponentRendererSimple(String icon) {
        this.icon = icon;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[]{icon};
    }

    @Override
    public void initialize(TileEntity ent) {
        ent.icon = icon;
    }
}
