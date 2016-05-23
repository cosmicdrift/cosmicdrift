/*
    Cosmic Drift is a computer game about building simulated space stations.
    Copyright (C) 2014-2015 Cel Skeggs and Christopher Quisling.

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
package io.github.cosmicdrift.cosmicdrift.graphics;

import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;
import io.github.cosmicdrift.cosmicdrift.components.ComponentLuaComputer;
import io.github.cosmicdrift.cosmicdrift.entities.EntityPlayer;
import io.github.cosmicdrift.cosmicdrift.items.Item;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;

public class Hud {

    private static final Font monospaced = new Font("Monospaced", Font.PLAIN, 14);
    private final World w;
    private int flash = 0;

    public Hud(World w) {
        this.w = w;
    }

    public void draw(Graphics g, EntityPlayer p, HashSet<Integer> pressed, HashSet<Integer> toggles) {
        flash++;
        boolean fl = flash % 8 < 4;
        // Picking up
        p.isInItemPickupMode = toggles.contains(KeyEvent.VK_SPACE);
        // Console
        FontMetrics fm = g.getFontMetrics();
        int cy = 200;
        g.setColor(Color.WHITE);
        for (ConsoleLine c : w.getConsoleLines()) {
            cy += fm.getHeight();
            g.drawString(c.line, 100, cy);
        }
        // Air
        g.setColor(fl && p.oxygen < 70 ? Color.CYAN : Color.BLUE);
        g.fillRoundRect(10, 80, 30, p.oxygen, 8, 8);
        g.setColor(Color.GRAY);
        g.drawRoundRect(10, 80, 30, 200, 8, 8);
        // Hitpoints
        g.setColor(fl && p.HP < 50 ? Color.ORANGE : Color.RED);
        g.fillRoundRect(600, 80, 30, p.HP * 2, 8, 8);
        g.setColor(Color.GRAY);
        g.drawRoundRect(600, 80, 30, 200, 8, 8);
        // Left and right hands
        g.setColor(p.isInItemPickupMode ? Color.YELLOW : Color.LIGHT_GRAY);
        g.drawRect(8, 420, 51, 51);
        Item il = p.inv.leftHand;
        if (il != null) {
            ResourceManager.drawObject(g, 9, 421, 50, 50, il.getIcon());
        }
        g.setColor(p.isInItemPickupMode ? Color.YELLOW : Color.LIGHT_GRAY);
        g.drawRect(580, 420, 51, 51);
        Item ir = p.inv.rightHand;
        if (ir != null) {
            ResourceManager.drawObject(g, 581, 421, 50, 50, ir.getIcon());
        }
        // Inventory
        if (toggles.contains(KeyEvent.VK_I)) {
            ArrayList<Item> itms = p.inv.inv;
            int i;
            for (i = 0; i < itms.size(); i++) {
                Item ic = itms.get(i);
                int x = i % 12;
                int y = i / 12;
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(13 + x * 51, 10 + y * 51, 51, 51);
                if (ic != null) {
                    ResourceManager.drawObject(g, 14 + x * 51, 11 + y * 51, 50, 50, ic.getIcon());
                }
            }
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(13 + (i % 12) * 51, 10 + (i / 12) * 51, 51, 51);
        }
        // Computer
        if (toggles.contains(KeyEvent.VK_BACK_QUOTE)) { // TODO: Display SOMETHING even if there's no computer, so that the user knows why their keys don't work.
            int bx = (p.x1 + p.x2) / 2 / Tile.TILE_SIZE, by = (p.y1 + p.y2) / 2 / Tile.TILE_SIZE;
            TileEntity b = getActiveComputer(bx, by);
            if (b != null) {
                Font saved = g.getFont();
                g.setFont(monospaced);
                FontMetrics cfm = g.getFontMetrics();
                String[] lines = b.getComponent(ComponentLuaComputer.class).getLines(b);
                int y = 100;
                g.setColor(Color.GRAY);
                g.fillRoundRect(55, 75, 533, 340, 10, 15);
                g.setColor(Color.BLACK);
                g.fillRoundRect(60, 80, 523, 330, 10, 15);
                g.setColor(Color.GREEN);
                for (String line : lines) {
                    if (line != null) {
                        g.drawString(line, 65, y);
                    }
                    y += cfm.getHeight();
                }
                g.setFont(saved);
            }
        }
    }

    public boolean press(int x, int y, int btn, EntityPlayer p, HashSet<Integer> pressed, HashSet<Integer> toggles) {
        if (toggles.contains(KeyEvent.VK_I)) {
            ArrayList<Item> itms = p.inv.inv;
            for (int i = 0; i <= itms.size(); i++) {
                int ix = i % 12;
                int iy = i / 12;
                if (14 + ix * 51 <= x && x < 65 + ix * 51 && 11 + iy * 51 <= y && y < 62 + iy * 51) {
                    // Use!
                    if (i == itms.size()) {
                        if (btn == 1 && p.inv.leftHand != null) {
                            itms.add(p.inv.leftHand);
                            p.inv.leftHand = null;
                        } else if (btn == 3 && p.inv.rightHand != null) {
                            itms.add(p.inv.rightHand);
                            p.inv.rightHand = null;
                        }
                    } else if (btn == 1 && p.inv.leftHand == null) {
                        p.inv.leftHand = itms.remove(i);
                    } else if (btn == 3 && p.inv.rightHand == null) {
                        p.inv.rightHand = itms.remove(i);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public void sendComputerKey(EntityPlayer p, char key) {
        int bx = (p.x1 + p.x2) / 2 / Tile.TILE_SIZE, by = (p.y1 + p.y2) / 2 / Tile.TILE_SIZE;
        TileEntity b = getActiveComputer(bx, by);
        if (b != null) {
            b.getComponent(ComponentLuaComputer.class).keyPress(b, key);
        }
    }

    private TileEntity getActiveComputer(int bx, int by) {
        for (int x = bx - 1; x <= bx + 1; x++) {
            for (int y = by - 1; y <= by + 1; y++) {
                for (TileEntity t : w.getTileEntities(x, y)) {
                    if (t.hasComponent(ComponentLuaComputer.class)) {
                        return t;
                    }
                }
            }
        }
        return null;
    }
}
