package io.github.cosmicdrift.cosmicdrift.components;

import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;

public class ComponentToggleOnEvent extends Component {

    private final String event;
    private final String variable;

    public ComponentToggleOnEvent(String event, String variable) {
        this.event = event;
        this.variable = variable;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[] { event, variable };
    }

    @Override
    public void onUserEvent(TileEntity ent, String name) {
        if (event.equals(name)) {
            ent.set(variable, !ent.<Boolean>get(variable));
        }
    }
}
