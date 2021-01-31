package net.wurstclient.mixinterface;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface CustomPayloadC2SPacketAccessor {
    ResourceLocation getChannel();

    PacketBuffer getData();
}
