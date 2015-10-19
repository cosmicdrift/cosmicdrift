package io.github.cosmicdrift.cosmicdrift.components;

import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;
import io.github.cosmicdrift.cosmicdrift.entities.EntityPlayer;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

public class ComponentMedkit extends Component {

    private final String supplyVariable;

    public ComponentMedkit(String supplyVariable) {
        this.supplyVariable = supplyVariable;
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[] {supplyVariable};
    }

    @Override
    public boolean onActivate(TileEntity ent, double dist) {
        final int supply = ent.get(supplyVariable);
        if (dist < Tile.TILE_SIZE * 3 && ent.getWorld().ply.HP < EntityPlayer.MAX_HP && supply > 0) {
            int lost = EntityPlayer.MAX_HP - ent.getWorld().ply.HP;
            if (lost > supply) {
                ent.getWorld().ply.HP += supply;
                ent.set(supplyVariable, 0);
            } else {
                ent.getWorld().ply.HP = EntityPlayer.MAX_HP;
                ent.set(supplyVariable, supply - lost);
            }
            return true;
        }
        return false;
    }
}
