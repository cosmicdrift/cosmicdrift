package io.github.cosmicdrift.cosmicdrift.computer;

import io.github.cosmicdrift.cosmicdrift.graphics.ResourceManager;
import io.github.cosmicdrift.cosmicdrift.networks.Packet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;

import io.github.cosmicdrift.virtlua.SimContext;
import io.github.cosmicdrift.virtlua.SimMessage;

public class LuaComputer {

    public static final String VERSION = "Lua-CPU-RevID-00002";
    public static final int ROWS = 19, COLS = 64;

    private static final SimContext template;

    static {
        try {
            template = new SimContext(ResourceManager.loadString("computer/bios.lua"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void forceInit() {
        // Do nothing. Just make sure that the init stuff got called.
    }

    public static HashMap<String, String> newDisk() throws IOException {
        HashMap<String, String> hm = new HashMap<>();
        InputStream manifest = LuaComputer.class.getResourceAsStream("/computer/lua-disk-manifest.txt");
        if (manifest == null) {
            throw new IOException("No virtual lua disk manifest found!");
        }
        BufferedReader manifestReader = new BufferedReader(new InputStreamReader(manifest));
        while (true) {
            String line = manifestReader.readLine();
            if (line == null) {
                break;
            }
            StringBuilder sb = new StringBuilder();
            try (BufferedReader dataReader = new BufferedReader(new InputStreamReader(LuaComputer.class.getResourceAsStream("/computer/" + line)))) {
                while (true) {
                    String dataLine = dataReader.readLine();
                    if (dataLine == null) {
                        break;
                    }
                    sb.append(dataLine).append('\n');
                }
            }
            hm.put(line, sb.toString());
        }
        return hm;
    }

    public final LinkedList<Packet> received = new LinkedList<>();
    public final LinkedList<Packet> sending = new LinkedList<>();

    private final SimContext context = new SimContext(template);
    private final short netID;
    private final String[] lines = new String[ROWS];
    private final HashMap<String, String> disk;

    public LuaComputer(short netID, HashMap<String, String> disk) {
        this.netID = netID;
        context.setSystemInfo(VERSION, (double) (netID & 0xFFFF), COLS + "x" + ROWS);
        this.disk = disk;
    }

    public void keyPress(byte b) {
        context.post(new SimMessage("key_press", (double) b));
    }

    public String[] getLines() {
        return lines;
    }

    private long totalTime = 0;
    private long totalSamples = 0;

    public void cycle(int ticks) {
        for (Packet p : received) {
            try {
                String dec = new String(p.data, "UTF-8");
                context.post(new SimMessage("net_recv", (double) (p.sourceID & 0xFFFF), dec, (double) (p.targetID & 0xFFFF)));
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
        }
        received.clear();
        long start = System.nanoTime();
        boolean more = context.simulate(ticks); // TODO: tweak this value
        long end = System.nanoTime();
        if (more && end > start) { // worrying about wraparound
            totalTime += end - start;
            if (++totalSamples == 100) {
                System.out.println("SIMULATE: " + totalTime / totalSamples + " (ns) average.");
                totalTime = 0;
                totalSamples = 0;
            }
        }
        while (true) {
            SimMessage msg = context.poll();
            if (msg == null) {
                break;
            }
            Object type = msg.get(0);
            if ("put_line".equals(type) && msg.get(1) instanceof Double && msg.get(2) instanceof String) {
                String line = (String) msg.get(2);
                if (line.length() > COLS) {
                    line = line.substring(0, COLS);
                }
                lines[Math.abs(((Double) msg.get(1)).intValue()) % ROWS] = line;
            } else if ("scroll_line".equals(type) && msg.get(1) instanceof Double) {
                int count = ((Double) msg.get(1)).intValue();
                if (count > 0 && count < lines.length) {
                    System.arraycopy(lines, 0, lines, count, lines.length - count);
                } else if (count < 0 && count > -lines.length) {
                    System.arraycopy(lines, -count, lines, 0, lines.length + count);
                }
            } else if ("net_send".equals(type) && msg.get(1) instanceof Double && msg.get(2) instanceof String) {
                try {
                    sending.add(new Packet(((String) msg.get(2)).getBytes("UTF-8"), netID, ((Double) msg.get(1)).shortValue()));
                } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex);
                }
            } else if ("disk_list".equals(type)) {
                StringBuilder sb = new StringBuilder();
                for (String filename : disk.keySet()) {
                    sb.append(filename).append("\n");
                }
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 1);
                }
                context.post(new SimMessage("disk_list", sb.toString()));
            } else if ("disk_read".equals(type) && msg.get(1) instanceof String) {
                context.post(new SimMessage("disk_read", msg.get(1), disk.get((String) msg.get(1))));
            } else if ("disk_write".equals(type) && msg.get(1) instanceof String && msg.get(2) instanceof String) {
                if (((String) msg.get(1)).contains("\n")) {
                    System.err.println("Discarded invalid disk write of filename with an embedded newline.");
                } else {
                    disk.put((String) msg.get(1), (String) msg.get(2));
                }
            } else {
                System.err.println("Unrecognized lua message type: " + type);
            }
        }
    }
}
