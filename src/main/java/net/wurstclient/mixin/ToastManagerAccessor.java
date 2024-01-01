package net.wurstclient.mixin;

import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Deque;

@Mixin(ToastManager.class)
public interface ToastManagerAccessor {

    @Accessor
    Deque<Toast> getToastQueue();

    @Accessor("toastQueue")
    @Mutable
    public void setToastQueue(Deque<Toast> toastQueue);
}