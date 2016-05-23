package io.github.cosmicdrift.cosmicdrift.components;

import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;

public class ComponentAccumulator extends Component {

    private final String destinationVar;
    private final String sourceVar;

    public ComponentAccumulator(String destinationVar, String sourceVar) {
        this.destinationVar = destinationVar;
        this.sourceVar = sourceVar;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[]{destinationVar, sourceVar};
    }

    @Override
    public void onTick(TileEntity ent) {
        ent.set(destinationVar, ent.<Double>get(destinationVar) + ent.<Double>get(sourceVar));
    }
}
