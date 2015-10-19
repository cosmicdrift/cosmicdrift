package cosmicdrift.components;

import cosmicdrift.compents.TileEntity;
import cosmicdrift.networks.Packet;

public abstract class Component {
    
    public void onTick(TileEntity ent) {
    }

    public void onUpdateNearby(TileEntity ent) {
    }

    public boolean onActivate(TileEntity ent, double dist) {
        return false;
    }

    public void initialize(TileEntity ent) {
    }

    public void onAdded(TileEntity ent) {
    }

    public void onRemove(TileEntity ent) {
    }

    public void onMessage(TileEntity ent, Packet pkt) {
    }

    public boolean isSolid(TileEntity ent) {
        return false;
    }

    public boolean beforeVariableChange(TileEntity ent, String var, Object o) {
        return false; // Don't deny it.
    }

    public void onVariableChange(TileEntity aThis, String var, Object o) {
        // Don't care.
    }

    public abstract Object[] saveAsConstructorArguments();

    public void presave(TileEntity aThis) {
        // Don't care.
    }

    public void postsave(TileEntity aThis) {
        // Don't care.
    }
}
