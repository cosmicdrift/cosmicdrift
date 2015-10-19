package io.github.cosmicdrift.cosmicdrift.networks;

public class Packet {

    public final byte[] data;
    public final short sourceID, targetID;

    public Packet(byte[] data, short sourceID, short targetID) {
        this.data = data;
        this.sourceID = sourceID;
        this.targetID = targetID;
    }
}
