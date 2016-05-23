package io.github.cosmicdrift.cosmicdrift.components;

import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;

public class ComponentAccumulationThreshold extends Component {

    private final double initial;
    private final String targetVar;
    private final double threshold;
    private final String event;

    public ComponentAccumulationThreshold(double initial, String targetVar, double threshold, String event) {
        this.initial = initial;
        this.targetVar = targetVar;
        this.threshold = threshold;
        this.event = event;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[]{initial, targetVar, threshold, event};
    }

    @Override
    public void initialize(TileEntity ent) {
        ent.set(targetVar, initial);
    }

    @Override
    public void onTick(TileEntity ent) {
        double value = ent.get(targetVar);
        if (value >= threshold) {
            ent.set(targetVar, initial);
            ent.onUserEvent(event);
        }
    }
}
