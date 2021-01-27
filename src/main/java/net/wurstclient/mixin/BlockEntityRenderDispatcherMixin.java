/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.tileentity.TileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.wurstclient.event.EventManager;
import net.wurstclient.events.RenderBlockEntityListener.RenderBlockEntityEvent;

@Mixin(TileEntityRendererDispatcher.class)
public class BlockEntityRenderDispatcherMixin
{
	@Inject(at = {@At("HEAD")},
		method = {
			"renderTileEntity(Lnet/minecraft/tileentity/TileEntity;FLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;)V"},
		cancellable = true)
	private <E extends TileEntity> void onRender(E blockEntity_1,
												 float float_1, MatrixStack matrixStack_1,
												 IRenderTypeBuffer vertexConsumerProvider_1, CallbackInfo ci)
	{
		RenderBlockEntityEvent event =
			new RenderBlockEntityEvent(blockEntity_1);
		EventManager.fire(event);
		
		if(event.isCancelled())
			ci.cancel();
	}
}
