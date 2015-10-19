package io.github.cosmicdrift.cosmicdrift.computer;

import io.github.cosmicdrift.cosmicdrift.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class BinComputerData {

    public static final byte[] bootstrap; // = new byte[]{(byte)0x03, (byte)0x0, (byte)0xa0, (byte)0x0, (byte)0x0, (byte)0x13,(byte)0x16, (byte)0xa, (byte)0x3A, (byte)0x9B, (byte)0x01, (byte)0x03, (byte)0x0, (byte)0x2c, (byte)0x16, (byte)0x3, (byte)0x0D, (byte)0x09, (byte)0x0, (byte)0x20, (byte)0x13, (byte)0x20, (byte)0x1C, (byte)0x16, (byte)0x3, (byte)0x10, (byte)0x17, (byte)0x40, (byte)0x0, (byte)0x1C, (byte)0x0F, (byte)0x0B, (byte)0x13, (byte)0x17, (byte)0x4, (byte)0x0, (byte)0xBA, (byte)0x3B, (byte)0x0B, (byte)0x01,(byte)0x17, (byte)0x3, (byte)0xc0, (byte)0x0B, (byte)0x16, (byte)0x3, (byte)0x0D, (byte)0x17, (byte)0x3, (byte)0xc0, (byte)0x7D, (byte)0x16, (byte)0x40, (byte)0x1C, (byte)0x16, (byte)0x3, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x14, (byte)0x0B, (byte)0x16, (byte)0x40, (byte)0x5C, (byte)0x1C, (byte)0x16, (byte)0x3, (byte)0x10, (byte)0x0B, (byte)0x13, (byte)0x0C, (byte)0x13, (byte)0x7B, (byte)0x01, (byte)0x01, (byte)0x0B, (byte)0x09, (byte)0x0, (byte)0x5, (byte)0x16, (byte)0x1, (byte)0x1C, (byte)0x03, (byte)0x0, (byte)0x45, (byte)0x17, (byte)0x4c, (byte)0x0, (byte)0x0D, (byte)0x9B, (byte)0x15, (byte)0x03, (byte)0x0, (byte)0x55, (byte)0x17, (byte)0x4c, (byte)0x2, (byte)0x0C, (byte)0x00, (byte)0x60, (byte)0x14,(byte)0x0B, (byte)0x13, (byte)0x20, (byte)0x7A, (byte)0xBB, (byte)0x01, (byte)0x00, (byte)0x12, (byte)0x0F, (byte)0x0B, (byte)0x12, (byte)0x13, (byte)0x09, (byte)0x0, (byte)0x55, (byte)0x13, (byte)0x09, (byte)0x0, (byte)0x5, (byte)0x09, (byte)0x0, (byte)0x39, (byte)0x12, (byte)0x0F, (byte)0x20, (byte)0x1C, (byte)0x12,(byte)0x20, (byte)0x3C, (byte)0x03, (byte)0x0, (byte)0x66, (byte)0x48, (byte)0x65, (byte)0x6c, (byte)0x6c, (byte)0x6f, (byte)0x2c, (byte)0x20, (byte)0x57, (byte)0x6f, (byte)0x72, (byte)0x6c, (byte)0x64, (byte)0x21, (byte)0xa, (byte)0x3e, (byte)0x20, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x16, (byte)0x3, (byte)0x16, (byte)0xa, (byte)0x09, (byte)0x0, (byte)0x3d, (byte)0x17, (byte)0x0, (byte)0x85, (byte)0x09, (byte)0x0, (byte)0x45, (byte)0x09, (byte)0x0, (byte)0x39, (byte)0x17, (byte)0x0, (byte)0x96, (byte)0x16, (byte)0xa, (byte)0x09, (byte)0x0, (byte)0x66, (byte)0x17, (byte)0x0, (byte)0x96, (byte)0x09, (byte)0x0, (byte)0x45,(byte)0x09, (byte)0x0, (byte)0x39, (byte)0x15, (byte)0x03, (byte)0x0, (byte)0xc1};

    static {
        StringBuilder sb = new StringBuilder();
        byte[] out;
        try {
            File bios = new File(new File("resources", "computer"), "bios.txt");
            InputStream resource = BinComputerData.class.getResourceAsStream("/computer/bios.txt");
            System.err.println("Resource: " + resource);
            try (Reader r = bios.exists() ? new FileReader(bios) : new InputStreamReader(resource)) {
                char[] data = new char[1024];
                while (true) {
                    int chars = r.read(data);
                    if (chars <= 0) {
                        break;
                    }
                    sb.append(data, 0, chars);
                }
            }
            out = Utils.loadHex(sb);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        bootstrap = out;
    }

    public static byte[] generateDisk() throws IOException {
        InputStream manifest = BinComputerData.class.getResourceAsStream("/computer/disk-manifest.txt");
        if (manifest == null) {
            throw new IOException("No virtual disk manifest found!");
        }
        BufferedReader manifestReader = new BufferedReader(new InputStreamReader(manifest));
        ArrayList<String> names = new ArrayList<>();
        ArrayList<InputStream> inputs = new ArrayList<>();
        while (true) {
            String line = manifestReader.readLine();
            if (line == null) {
                break;
            }
            names.add(line);
            inputs.add(BinComputerData.class.getResourceAsStream("/computer/" + line)); // TODO: Make sure that these are always closed.
        }
        System.out.println("# inserts: " + names.size());
        // Default disk for testing purposes
        byte[] disk = new byte[32768];
        ByteBuffer dt = ByteBuffer.wrap(disk);
        dt.put(new byte[]{'C', 'o', 's', 'm', 'i', 'c', '_', 'D', 'r', 'i', 'f', 't', 'T', 'e', 's', 't', 'i', 'n', 'g', ' ', 'V', 'o', 'l', 'm', 0, 0, 0, 0, 0, 0, 0, 0});
        for (String filename : names) {
            byte[] name = filename.substring("default_disk_".length()).getBytes();
            dt.put(name);
            if (name.length > 8) {
                throw new IOException("Imported name too long: " + filename);
            }
            for (int i = name.length; i < 8; i++) {
                dt.put((byte) 0);
            }
        }
        if (dt.position() != 32 + 8 * (names.size())) {
            throw new RuntimeException("Wait what");
        }
        for (int i = names.size(); i < 124; i++) {
            dt.putLong(0);
        }
        if (dt.position() != 1024) {
            throw new RuntimeException("Wait what");
        }
        byte[] tbuf = new byte[255];
        for (InputStream in : inputs) {
            int i = 0;
            int o;
            while (i < tbuf.length) {
                o = in.read(tbuf, i, tbuf.length - i);
                if (o < 0) {
                    while (i < tbuf.length) {
                        tbuf[i++] = 0;
                    }
                    break;
                } else if (o == 0) {
                    throw new IOException("Expected != 0!");
                }
                i += o;
            }
            if (in.read() != -1) {
                throw new IOException("Expected EOF in " + names.get(i) + "- is the file longer than 255 bytes?");
            }
            dt.put(tbuf);
            dt.put((byte) 1);
            in.close();
        }
        if (dt.position() != 1024 + 256 * names.size()) {
            throw new RuntimeException("Wait what");
        }
        return disk;
    }
}
