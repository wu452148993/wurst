/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.world.BlockRenderView;
import net.minecraftforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.event.EventManager;
import net.wurstclient.events.TesselateBlockListener.TesselateBlockEvent;


@Mixin(BlockModelRenderer.class)
public class TerrainRenderContextMixin
{
	@Inject(at = @At("HEAD"),
			method = "tesselateBlock(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZLnet/minecraft/util/math/random/Random;JILnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/render/RenderLayer;Z)V",
		cancellable = true)
	private void onTessellateBlock(BlockRenderView arg, BakedModel arg2, BlockState arg3, BlockPos arg4,
								   MatrixStack arg5, VertexConsumer arg6, boolean bl,
								   net.minecraft.util.math.random.Random arg7, long l, int i, ModelData modelData,
								   RenderLayer renderType, boolean queryModelSpecificData, CallbackInfo ci)
	{
		TesselateBlockEvent event = new TesselateBlockEvent(arg3);
		EventManager.fire(event);
		
		if(event.isCancelled())
			ci.cancel();
	}
}
