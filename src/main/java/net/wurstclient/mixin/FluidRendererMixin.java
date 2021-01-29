/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.FluidBlockRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.wurstclient.event.EventManager;
import net.wurstclient.events.ShouldDrawSideListener.ShouldDrawSideEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidBlockRenderer.class)
public class FluidRendererMixin
{
	@Inject(at = {@At("HEAD")},
		method = {
			"func_239283_a_(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;F)Z"},
		cancellable = true)
	private static void onIsSideCovered(IBlockReader blockView_1,
										BlockPos blockPos_1, Direction direction_1, float float_1,
										CallbackInfoReturnable<Boolean> cir)
	{
		BlockState state = blockView_1.getBlockState(blockPos_1);
		ShouldDrawSideEvent event = new ShouldDrawSideEvent(state);
		EventManager.fire(event);
		
		if(event.isRendered() != null)
			cir.setReturnValue(!event.isRendered());
	}
}
