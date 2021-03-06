/*
    Cosmic Drift is a computer game about building simulated space stations.
    Copyright (C) 2014-2015 Cel Skeggs.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.cosmicdrift.cosmicdrift.components;

import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;
import io.github.cosmicdrift.cosmicdrift.networks.Packet;

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

    public void onUserEvent(TileEntity ent, String name) {
        // Don't care
    }
}
