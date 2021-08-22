/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.IScreen;
import net.minecraft.network.play.client.CChatMessagePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.wurstclient.WurstClient;

@Mixin(Screen.class)
public abstract class ScreenMixin extends FocusableGui
		implements IScreen, IRenderable
{
	@Inject(at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;sendChatMessage(Ljava/lang/String;)V",
		ordinal = 0),
		method = {"sendMessage(Ljava/lang/String;Z)V"},
		cancellable = true)
	private void onSendChatMessage(String message, boolean toHud,
		CallbackInfo ci)
	{
		if(toHud)
			return;

		CChatMessagePacket packet = new CChatMessagePacket(message);
		WurstClient.MC.getConnection().sendPacket(packet);
		ci.cancel();
	}

	@Inject(at = {@At("HEAD")},
			method = {
					"renderBackground(Lcom/mojang/blaze3d/matrix/MatrixStack;)V"},
			cancellable = true)
	public void onRenderBackground(MatrixStack matrices, CallbackInfo ci)
	{
		if(WurstClient.INSTANCE.getHax().noBackgroundHack.isEnabled())
			ci.cancel();
	}
}
