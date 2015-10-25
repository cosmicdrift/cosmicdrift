/*
    Cosmic Drift is a computer game about building simulated space stations.
    Copyright (C) 2014-2015 Colby Skeggs and Christopher Quisling.

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

import io.github.cosmicdrift.cosmicdrift.Chunk;
import io.github.cosmicdrift.cosmicdrift.Inventory;
import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.compents.TileEntity;
import io.github.cosmicdrift.cosmicdrift.computer.LuaComputer;
import io.github.cosmicdrift.cosmicdrift.entities.Entity;
import io.github.cosmicdrift.cosmicdrift.entities.EntityPlayer;
import io.github.cosmicdrift.cosmicdrift.items.Item;
import io.github.cosmicdrift.cosmicdrift.tiles.Tile;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JPanel { // TODO: Make negative coordinates work better.

    private static final BufferedImage menu = ResourceManager.loadImage("menu_2.png");
    private static final BufferedImage death = ResourceManager.loadImage("death_screen.png");
    private static final Clip gameSoundtrack = ResourceManager.loadSound("health_50w_110.wav");
    private static final Clip menuSoundtrack = ResourceManager.loadSound("title_track.wav");
    private static final Clip deathNotification = ResourceManager.loadSound("died.wav");

    private World world;
    private Hud hud;
    private boolean playedDeadSound = false;
    private boolean saveWasPressed = false;
    private final HashSet<Integer> pressedKeys = new HashSet<>();
    private final HashSet<Integer> toggledOnKeys = new HashSet<>();
    private final BufferedImage imageBuffer;
    private final Object synchObj = new Object();

    public Main(int w, int h) {
        imageBuffer = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
    }

    public static void main(String[] args) {
        Logger.getLogger("").getHandlers()[0].setLevel(Level.FINE);
        JFrame j = new JFrame();
        j.setSize(646, 508); // TODO: Figure out why these dimensions are needed.
        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final Main m = new Main(j.getWidth(), j.getHeight());
        j.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int kc = e.getKeyCode();
                if (m.toggledOnKeys.contains(KeyEvent.VK_BACK_QUOTE) && kc != KeyEvent.VK_BACK_QUOTE) {
                    return;
                }
                m.pressedKeys.add(kc);
                if (!m.toggledOnKeys.remove(kc)) {
                    m.toggledOnKeys.add(kc);
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (m.toggledOnKeys.contains(KeyEvent.VK_BACK_QUOTE)) {
                    char key = e.getKeyChar();
                    if (key != KeyEvent.CHAR_UNDEFINED && key != '`') {
                        if (key == 13) {
                            key = 10;
                        }
                        m.hud.sendComputerKey(m.world.ply, key);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                m.pressedKeys.remove(e.getKeyCode());
            }
        });
        m.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                synchronized (m.synchObj) {
                    m.press(e.getX() - 3, e.getY() - 14, e.getButton());
                }
            }
        });
        j.setContentPane(m);
        j.setResizable(false);
        j.setVisible(true);
        m.startLoop();
    }

    public void startLoop() {
        new Timer().schedule(new TimerTask() {
            private int frames = 0;
            private long start = System.currentTimeMillis();

            @Override
            public void run() {
                synchronized (synchObj) {
                    if (world != null) {
                        update();
                        if (world.ply.isDead() && !playedDeadSound) {
                            playedDeadSound = true;
                            if (deathNotification != null) {
                                deathNotification.start();
                            }
                            if (gameSoundtrack != null) {
                                gameSoundtrack.stop();
                            }
                        }
                        frames++;
                        if (frames % 100 == 0) {
                            long time = System.currentTimeMillis() - start;
                            System.out.println("FPS: " + frames + " in " + (time / 1000f) + " = " + (1000 * frames / time) + " FPS");
                        }
                    }
                    render(imageBuffer.getWidth(), imageBuffer.getHeight(), imageBuffer.createGraphics());
                }
            }
        }, 50, 50);
        // Can run at over 50 FPS.
        if (menuSoundtrack != null) {
            menuSoundtrack.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void press(int x, int y, int btn) {
        if (world == null) {
            if (x >= 130 && y >= 300 && x <= 300 && y <= 420) {
                // TODO: Figure out better button bounding box
                LuaComputer.forceInit();
                try {
                    world = WorldGenerator.create();
                } catch (IOException ex) {
                    throw new RuntimeException("Could not build world!", ex);
                }
                hud = new Hud(world);
                world.ply = new EntityPlayer(308 * Tile.TILE_SIZE, 308 * Tile.TILE_SIZE, world, Inventory.defaultInventory(world));
                world.addEntity(world.ply);
                world.print("Welcome to Cosmic Drift!");
                if (menuSoundtrack != null) {
                    menuSoundtrack.stop();
                }
                if (gameSoundtrack != null) {
                    gameSoundtrack.loop(Clip.LOOP_CONTINUOUSLY);
                }
            } else if (x >= 300 && y >= 300 && x <= 530 && y <= 420) {
                try {
                    world = World.load("savefile.sxp");
                } catch (IOException ex) {
                    throw new RuntimeException("Could not load world!", ex);
                }
                hud = new Hud(world);
                world.print("Welcome back to Cosmic Drift!");
                if (menuSoundtrack != null) {
                    menuSoundtrack.stop();
                }
                if (gameSoundtrack != null) {
                    gameSoundtrack.loop(Clip.LOOP_CONTINUOUSLY);
                }
            }
        } else if (!hud.press(x, y, btn, world.ply, pressedKeys, toggledOnKeys)) {
            int w = getWidth(), h = getHeight();
            EntityPlayer p = world.ply;
            int relX = (p.x1 + p.x2) / 2 - w / 2, relY = (p.y1 + p.y2) / 2 - h / 2;
            x += relX;
            y += relY;
            int dx = x - (p.x1 + p.x2) / 2, dy = y - (p.y1 + p.y2) / 2;
            double dist = Math.sqrt(dx * dx + dy * dy);
            x /= Tile.TILE_SIZE;
            y /= Tile.TILE_SIZE;
            if (btn == 1) {
                Item lh = p.inv.leftHand;
                if (lh == null) {
                    Tile.activate(world, world.getTile(x, y), x, y, dist);
                }
                if (lh != null && lh.use(world, p, x, y, dist)) {
                    p.inv.leftHand = null;
                }
            } else if (btn == 3) {
                Item lh = p.inv.rightHand;
                if (lh == null) {
                    Tile.activate(world, world.getTile(x, y), x, y, dist);
                }
                if (lh != null && lh.use(world, p, x, y, dist)) {
                    p.inv.rightHand = null;
                }
            }
        }
    }

    private static final boolean dvorak = System.getProperty("user.name").equals("colby") || System.getProperty("user.name").equals("skeggsc");

    private void update() {
        boolean left = pressedKeys.contains(dvorak ? KeyEvent.VK_A : KeyEvent.VK_A);
        boolean right = pressedKeys.contains(dvorak ? KeyEvent.VK_E : KeyEvent.VK_D);
        boolean up = pressedKeys.contains(dvorak ? KeyEvent.VK_COMMA : KeyEvent.VK_W);
        boolean down = pressedKeys.contains(dvorak ? KeyEvent.VK_O : KeyEvent.VK_S);
        float rx = (right ? 1 : 0) - (left ? 1 : 0);
        float ry = (down ? 1 : 0) - (up ? 1 : 0);
        float rel = (float) Math.sqrt(rx * rx + ry * ry);
        EntityPlayer p = world.ply;
        if (rel != 0) {
            boolean hit = p.hasStep(rx > 0 ? -1 : rx < 0 ? 1 : 0, ry > 0 ? -1 : ry < 0 ? 1 : 0);
            int m = hit ? 18 : 2;
            rx *= m / rel;
            ry *= m / rel;
            p.vX += rx;
            p.vY += ry;
        }
        if (pressedKeys.contains(KeyEvent.VK_Z)) {
            if (p.inv.leftHand != null) {
                world.dropItemAtPixel((p.x1 + p.x2) / 2, (p.y1 + p.y2) / 2, p.inv.leftHand);
                p.inv.leftHand = null;
            }
        }
        if (pressedKeys.contains(KeyEvent.VK_C)) {
            if (p.inv.rightHand != null) {
                world.dropItemAtPixel((p.x1 + p.x2) / 2, (p.y1 + p.y2) / 2, p.inv.rightHand);
                p.inv.rightHand = null;
            }
        }
        if (pressedKeys.contains(KeyEvent.VK_L) != saveWasPressed) {
            saveWasPressed = !saveWasPressed;
            if (saveWasPressed) {
                try {
                    world.save("savefile.sxp");
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Error while saving", ex);
                }
            }
        }
        world.tick();
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(imageBuffer, 0, 0, this);
    }

    private void render(int w, int h, Graphics g) {
        if (world == null) {
            g.drawImage(menu, 0, 0, this);
        } else if (world.ply.isDead()) {
            g.drawImage(death, 0, 0, this);
        } else {
            g.setColor(new Color(10, 10, 10));
            g.fillRect(0, 0, w, h);
            EntityPlayer p = world.ply;
            int relX = w / 2 - (p.x1 + p.x2) / 2, relY = h / 2 - (p.y1 + p.y2) / 2;
            for (Chunk c : world.getChunkList()) {
                int minX = (c.cx * Chunk.CHUNK_SIZE) * Tile.TILE_SIZE + relX;
                int minY = (c.cy * Chunk.CHUNK_SIZE) * Tile.TILE_SIZE + relY;
                int maxX = minX + Chunk.CHUNK_SIZE * Tile.TILE_SIZE;
                int maxY = minY + Chunk.CHUNK_SIZE * Tile.TILE_SIZE;
                if (maxX < 0 || maxY < 0 || minX >= w || minY >= h) {
                    continue; // Outside of screen - don't render. TODO: Apply this more rigorously.
                }
                for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
                    for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
                        int gx = c.cx * Chunk.CHUNK_SIZE + x;
                        int gy = c.cy * Chunk.CHUNK_SIZE + y;
                        int baseX = gx * Tile.TILE_SIZE + relX;
                        int baseY = gy * Tile.TILE_SIZE + relY;
                        if (c.getTile(x, y) == null) {
                            ResourceManager.drawObject(g, baseX, baseY, Tile.TILE_SIZE, Tile.TILE_SIZE, "space.png");
                        } else {
                            ResourceManager.drawObject(g, baseX, baseY, Tile.TILE_SIZE, Tile.TILE_SIZE, "wall_back.png");
                        }
                        for (TileEntity ent : c.getTileEntities(x, y)) {
                            ResourceManager.drawObject(g, baseX, baseY, Tile.TILE_SIZE, Tile.TILE_SIZE, ent.getIcon());
                        }
                    }
                }
            }
            for (Chunk c : world.getChunkList()) {
                for (Entity e : c.entities) {
                    ResourceManager.drawObject(g, e.x1 + relX, e.y1 + relY, e.x2 - e.x1, e.y2 - e.y1, e.getIcon());
                }
            }
            hud.draw(g, world.ply, pressedKeys, toggledOnKeys);
        }
        repaint();
    }
}
