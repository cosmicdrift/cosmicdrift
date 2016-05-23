package io.github.cosmicdrift.cosmicdrift.components;

import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;

public class ComponentSetOnEvent extends Component {

    private final String event;
    private final String variable;
    private final Object value;

    public ComponentSetOnEvent(String event, String variable, Object value) {
        this.event = event;
        this.variable = variable;
        this.value = value;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[] { event, variable, value };
    }

    @Override
    public void onUserEvent(TileEntity ent, String name) {
        if (event.equals(name)) {
            ent.set(variable, value);
        }
    }
}
