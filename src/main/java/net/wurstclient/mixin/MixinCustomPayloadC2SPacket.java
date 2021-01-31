package net.wurstclient.mixin;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.util.ResourceLocation;
import net.wurstclient.mixinterface.CustomPayloadC2SPacketAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({CCustomPayloadPacket.class})
public class MixinCustomPayloadC2SPacket implements CustomPayloadC2SPacketAccessor {
    @Shadow
    private ResourceLocation channel;
    @Shadow
    private PacketBuffer data;

    public MixinCustomPayloadC2SPacket() {
    }

    public ResourceLocation getChannel() {
        return this.channel;
    }

    public PacketBuffer getData() {
        return new PacketBuffer(this.data.copy());
    }
}
