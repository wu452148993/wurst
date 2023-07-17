/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.wurstclient.WurstClient;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin
{
	@ModifyConstant(
		method = "renderFireOverlay(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/util/math/MatrixStack;)V",
		// -0.3F converted to double, because MatrixStack.translate()
		// doesn't take floats in this version
		constant = @Constant(doubleValue = -0.30000001192092896))
	private static double getFireOffset(double orig)
	{
		return orig - WurstClient.INSTANCE.getHax().noFireOverlayHack
			.getOverlayOffset();
	}
	
	@Inject(at = @At("HEAD"),
		method = "renderUnderwaterOverlay(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/util/math/MatrixStack;)V",
		cancellable = true)
	private static void onRenderUnderwaterOverlay(
		MinecraftClient minecraftClient, MatrixStack matrixStack,
		CallbackInfo ci)
	{
		if(WurstClient.INSTANCE.getHax().noOverlayHack.isEnabled())
			ci.cancel();
	}
}
