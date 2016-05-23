package io.github.cosmicdrift.cosmicdrift.utils;

import io.github.cosmicdrift.cosmicdrift.components.*;
import io.github.cosmicdrift.cosmicdrift.entities.Entity;
import io.github.cosmicdrift.cosmicdrift.entities.EntityItem;
import io.github.cosmicdrift.cosmicdrift.entities.EntityPlayer;
import io.github.cosmicdrift.cosmicdrift.items.*;

public class Registries {
    public static final ClassRegistry<Component> componentRegistry = new ClassRegistry<>(Component.class);

    static {
        componentRegistry.register(ComponentAccumulationThreshold.class);
        componentRegistry.register(ComponentAccumulator.class);
        componentRegistry.register(ComponentActivationSynchronizer.class);
        componentRegistry.register(ComponentAirExchanger.class);
        componentRegistry.register(ComponentDispenser.class);
        componentRegistry.register(ComponentGravityGenerator.class);
        componentRegistry.register(ComponentLuaComputer.class);
        componentRegistry.register(ComponentMarkerWall.class);
        componentRegistry.register(ComponentMedkit.class);
        componentRegistry.register(ComponentNetworkData.class);
        componentRegistry.register(ComponentNetworkDataEndpoint.class);
        componentRegistry.register(ComponentNetworkFluid.class);
        componentRegistry.register(ComponentNetworkPower.class);
        componentRegistry.register(ComponentPowerMinimum.class);
        componentRegistry.register(ComponentRendererDirectioned.class);
        componentRegistry.register(ComponentRendererDual.class);
        componentRegistry.register(ComponentRendererSimple.class);
        componentRegistry.register(ComponentSetOnEvent.class);
        componentRegistry.register(ComponentButton.class);
        componentRegistry.register(ComponentToggleOnEvent.class);
    }

    public static final ClassRegistry<Entity> entityRegistry = new ClassRegistry<>(Entity.class);

    static {
        entityRegistry.register(EntityItem.class);
        entityRegistry.register(EntityPlayer.class);
    }

    public static final ClassRegistry<Item> itemRegistry = new ClassRegistry<>(Item.class);

    static {
        itemRegistry.register(ItemFluidsMonitor.class);
        itemRegistry.register(ItemNetworkMonitor.class);
        itemRegistry.register(ItemPlating.class);
        itemRegistry.register(ItemTileEntity.class);
        itemRegistry.register(ItemWrench.class);
    }
}
