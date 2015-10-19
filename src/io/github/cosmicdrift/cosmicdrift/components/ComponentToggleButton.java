package io.github.cosmicdrift.cosmicdrift.components;

import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ComponentToggleButton extends Component {

    private static final Logger logger = Logger.getLogger("ComponentToggleButton");

    private final String control;
    private final int minimum;
    private final int maximum;

    public ComponentToggleButton(String control, int minimum, int maximum) {
        this.control = control;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[] {control, minimum, maximum};
    }

    @Override
    public boolean onActivate(TileEntity ent, double dist) {
        if (dist >= minimum * Tile.TILE_SIZE && dist <= maximum * Tile.TILE_SIZE) {
            logger.log(Level.INFO, "Activated {0} at {1}", new Object[]{ent, dist});
            if (!ent.set(control, !ent.<Boolean>get(control))) {
                logger.info("Failed to activate");
            }
            return true;
        } else {
            logger.log(Level.INFO, "Failed to activate {0} at {1}", new Object[]{ent, dist});
            return false;
        }
    }

}
