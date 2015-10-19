package io.github.cosmicdrift.cosmicdrift.components;

import io.github.cosmicdrift.cosmicdrift.compents.NetworkType;
import io.github.cosmicdrift.cosmicdrift.networks.Packet;

import java.util.Random;

public class ComponentNetworkData extends ComponentNetwork<NetworkType.DataNetwork> {

    private static final Random rand = new Random();
    
    public static synchronized short generateID(Object obj) {
        return (short) rand.nextInt();
    }

    public static int decodeMessage(Packet pkt, int netID, String... messages) {
        if (pkt.targetID != 0 && pkt.targetID != netID) {
            return -2;
        }
        outer:
        for (int i = 0; i < messages.length; i++) {
            byte[] data = messages[i].getBytes();
            int j;
            for (j = 0; j < data.length; j++) {
                if (data[j] != pkt.data[j]) {
                    continue outer;
                }
            }
            if (j < pkt.data.length && pkt.data[j] != 0) {
                continue;
            }
            return i;
        }
        return -1;
    }

    public ComponentNetworkData() {
        super(NetworkType.DATA);
    }

    @Override
    public Object[] saveAsConstructorArguments() {
        return new Object[0];
    }
}
