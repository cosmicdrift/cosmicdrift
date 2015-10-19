package io.github.cosmicdrift.cosmicdrift.components;

import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;

public class ComponentRendererDual extends Component {

    private final String control;
    private final String enabled;
    private final String disabled;

    public ComponentRendererDual(String control, String enabled, String disabled) {
        this.control = control;
        this.enabled = enabled;
        this.disabled = disabled;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[]{control, enabled, disabled};
    }

    @Override
    public void initialize(TileEntity ent) {
        ent.icon = ent.get(control) ? enabled : disabled;
    }

    @Override
    public void onVariableChange(TileEntity ent, String var, Object value) {
        if (control.equals(var)) {
            ent.icon = ((Boolean) value) ? enabled : disabled;
        }
    }
}
