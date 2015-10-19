package io.github.cosmicdrift.cosmicdrift.entities;

import io.github.cosmicdrift.cosmicdrift.InventoryIO;
import io.github.cosmicdrift.cosmicdrift.World;
import io.github.cosmicdrift.cosmicdrift.dataio.CompoundBaseIO;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeReader;
import io.github.cosmicdrift.cosmicdrift.dataio.STreeWriter;

import java.io.IOException;

public class EntityPlayerIO extends CompoundBaseIO<EntityPlayer> {

    private final World world;
    private final InventoryIO inventoryIO;

    public EntityPlayerIO(World world) {
        super("player");
        this.world = world;
        this.inventoryIO = new InventoryIO(world);
    }

    @Override
    protected EntityPlayer loadEnd(STreeReader reader) throws IOException {
        EntityPlayer out = new EntityPlayer(reader.readInteger(), reader.readInteger(), world, inventoryIO.load(reader));
        out.vX = reader.readInteger();
        out.vY = reader.readInteger();
        out.HP = reader.readInteger();
        out.oxygen = reader.readInteger();
        out.facingLeft = reader.readBoolean();
        out.isInItemPickupMode = reader.readBoolean();
        reader.endList();
        return out;
    }

    @Override
    protected void saveMiddle(STreeWriter writer, EntityPlayer data) throws IOException {
        writer.writeInteger(data.x1);
        writer.writeInteger(data.y1);
        inventoryIO.save(writer, data.inv);
        writer.writeInteger(data.vX);
        writer.writeInteger(data.vY);
        writer.writeInteger(data.HP);
        writer.writeInteger(data.oxygen);
        writer.writeBoolean(data.facingLeft);
        writer.writeBoolean(data.isInItemPickupMode);
    }

    @Override
    public boolean accepts(Object o) {
        return o instanceof EntityPlayer;
    }
}
