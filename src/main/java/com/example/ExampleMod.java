package com.example;

import net.neoforged.fml.common.Mod;
import net.wurstclient.WurstInitializer;
import net.wurstclient.test.WurstE2ETestClient;

@Mod(ExampleMod.MOD_ID)
public final class ExampleMod {
    public static final String MOD_ID = "wurst";

    public ExampleMod() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like registries and resources) may still be uninitialized.
        // Proceed with mild caution.
        WurstInitializer wurstInitializer = new WurstInitializer();
        wurstInitializer.onInitialize();

        WurstE2ETestClient wurstE2ETestClient = new WurstE2ETestClient();
        wurstE2ETestClient.onInitialize();
    }
}
