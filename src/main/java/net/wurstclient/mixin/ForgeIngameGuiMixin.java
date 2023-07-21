package net.wurstclient.mixin;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.wurstclient.WurstClient;
import net.wurstclient.event.EventManager;
import net.wurstclient.events.GUIRenderListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeGui.class)
public class ForgeIngameGuiMixin extends DrawableHelper {

    @Inject(
            at = {@At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V",
                    ordinal = 0)},
            method = {"render(Lnet/minecraft/client/util/math/MatrixStack;F)V"})
    private void onRender(MatrixStack matrices, float tickDelta,
                          CallbackInfo ci)
    {
        if(WurstClient.MC.options.hudHidden)
            return;

        if(WurstClient.MC.options.debugEnabled)
            return;

        GUIRenderListener.GUIRenderEvent event = new GUIRenderListener.GUIRenderEvent(matrices, tickDelta);
        EventManager.fire(event);
    }
}

