package cosmicdrift.components;

import cosmicdrift.compents.TileEntity;

public class ComponentActivationSynchronizer extends Component {

    private final String variable;

    public ComponentActivationSynchronizer(String variable) {
        this.variable = variable;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[] {variable};
    }

    @Override
    public void onVariableChange(TileEntity ent, String var, Object value) {
        if (variable.equals(var)) {
            final boolean state = (boolean) value;
            int[][] ts = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] c : ts) {
                for (TileEntity t : ent.getWorld().getTileEntities(ent.x + c[0], ent.y + c[1])) {
                    if (t.type.typename.equals(ent.type.typename)) { // TODO: Is this how synchronization should be choosen?
                        if (state != t.<Boolean>get(variable)) {
                            t.set(variable, state);
                        }
                    }
                }
            }
        }
    }
}
