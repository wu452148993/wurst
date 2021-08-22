package net.wurstclient.mixin;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({CCustomPayloadPacket.class})
public interface CustomPayloadC2SPacketAccessor
{
    @Accessor
    ResourceLocation getChannel();

    @Accessor
    PacketBuffer getData();
}
