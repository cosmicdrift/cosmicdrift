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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class ResourceManager {

    private static final HashMap<String, BufferedImage> loaded = new HashMap<>();
    private static final BufferedImage error;

    private static InputStream getResource(String fname, boolean buffer) throws IOException {
        InputStream in = ResourceManager.class.getResourceAsStream("/" + fname);
        if (in == null) {
            throw new FileNotFoundException("Cannot find file resource: " + fname);
        }
        if (!buffer) {
            return in;
        } else {
            ArrayList<byte[]> inputChunks = new ArrayList<>();
            byte[] bytes = new byte[4096];
            int total = 0;
            while (true) {
                int found = in.read(bytes);
                if (found == -1) {
                    break;
                }
                total += found;
                inputChunks.add(Arrays.copyOf(bytes, found));
            }
            byte[] combined = new byte[total];
            int pos = 0;
            for (byte[] chunk : inputChunks) {
                System.arraycopy(chunk, 0, combined, pos, chunk.length);
                pos += chunk.length;
            }
            if (pos != total) {
                throw new RuntimeException("Somehow, the wrong number of bytes were read."); // Should never happen.
            }
            return new ByteArrayInputStream(combined);
        }
    }

    static {
        try {
            error = ImageIO.read(getResource("error.png", false));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static BufferedImage loadImage(String s) {
        BufferedImage i = loaded.get(s);
        if (i != null) {
            return i;
        }
        try {
            i = ImageIO.read(getResource(s, false));
        } catch (IOException ex) {
            System.err.println("Cannot load " + s);
            ex.printStackTrace();
            i = error;
        }
        loaded.put(s, i);
        return i;
    }

    public static void drawObject(Graphics g, int x, int y, int w, int h, String icon) {
        if (icon == null) {
            g.setColor(Color.PINK);
            g.fillRect(x, y, w, h);
        } else {
            g.drawImage(ResourceManager.loadImage(icon), x, y, null);
        }
    }

    public static Clip loadSound(String s) {
        try {
            // Troubleshooting note for linux: make sure you have icedtea-sound installed.
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(getResource(s, true));
            AudioFormat format = audioIn.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioIn);
            return clip;
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException ex) {
            ex.printStackTrace();
            System.err.println("WARNING: Could not load sound: " + s);
            return null;
        }
    }

    public static String loadString(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getResource(path, false)))) {
            StringBuilder builder = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                builder.append(line).append('\n');
            }
            return builder.toString();
        }
    }

    public static InputStream loadInputStream(String path) throws IOException {
        return getResource(path, false);
    }

    public static Reader loadReader(String path) throws IOException {
        return new InputStreamReader(loadInputStream(path));
    }
}
