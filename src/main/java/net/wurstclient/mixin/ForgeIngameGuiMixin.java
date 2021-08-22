package net.wurstclient.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.wurstclient.WurstClient;
import net.wurstclient.event.EventManager;
import net.wurstclient.events.GUIRenderListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeIngameGui.class)
public class ForgeIngameGuiMixin extends AbstractGui {

    @Inject(
            at = {@At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V",
                    ordinal = 1)},
            method = {"renderIngameGui(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V"})
    private void onRender(MatrixStack matrixStack, float partialTicks,
                          CallbackInfo ci)
    {
        if(WurstClient.MC.gameSettings.hideGUI)
            return;

        if(WurstClient.MC.gameSettings.showDebugInfo)
            return;

        GUIRenderListener.GUIRenderEvent event = new GUIRenderListener.GUIRenderEvent(matrixStack, partialTicks);
        EventManager.fire(event);
    }
}

