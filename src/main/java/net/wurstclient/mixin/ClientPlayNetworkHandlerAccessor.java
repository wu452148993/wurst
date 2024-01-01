package net.wurstclient.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.toast.Toast;
import net.minecraft.network.encryption.ClientPlayerSession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Deque;

@Mixin(ClientPlayNetworkHandler.class)
public interface ClientPlayNetworkHandlerAccessor {

    @Accessor
    ClientPlayerSession getSession();

    @Accessor("session")
    @Mutable
    public void setSession(ClientPlayerSession session);
}