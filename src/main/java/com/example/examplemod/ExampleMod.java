package com.example.examplemod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.wurstclient.WurstClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("wurst")
public class ExampleMod {

    public static final Logger LOGGER = LogManager.getLogger();
    private static boolean initialized;
    public ExampleMod() {

        // This is our mod's event bus, used for things like registry or lifecycle events
        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();

        // This listener is fired on both client and server during setup.
        MOD_BUS.addListener(this::commonSetup);
        // This listener is only fired during client setup, so we can use client-side methods here.
        MOD_BUS.addListener(this::clientSetup);

        // Most other events are fired on Forge's bus.
        // If we want to use annotations to register event listeners,
        // we need to register our object like this!
        MinecraftForge.EVENT_BUS.register(this);

        if(initialized)
            throw new RuntimeException(
                    "WurstInitializer.onInitialize() ran twice!");

        WurstClient.INSTANCE.initialize();
        initialized = true;

        // For more information on how to deal with events in Forge,
        // like automatically subscribing an entire class to an event bus
        // or using static methods to listen to events,
        // feel free to check out the Forge wiki!
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Hello from common setup! This is *after* registries are done, so we can do this:");
        LOGGER.info("Look, I found a {}!", Items.DIAMOND);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Hey, we're on Minecraft version {}!", MinecraftClient.getInstance().getGameVersion());
    }

    @SubscribeEvent
    public void kaboom(ExplosionEvent.Detonate event) {
        LOGGER.info("Kaboom! Something just blew up in {}!", event.getLevel());
    }
}
