/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.event.EventManager;
import net.wurstclient.events.ShouldDrawSideListener.ShouldDrawSideEvent;
import net.wurstclient.events.TesselateBlockListener.TesselateBlockEvent;

@Mixin(BlockModelRenderer.class)
public abstract class BlockModelRendererMixin
{
    @Inject(at = {@At("HEAD")},
            method = {
                    "renderModelSmooth(Lnet/minecraft/world/IBlockDisplayReader;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z",
                    "renderModelFlat(Lnet/minecraft/world/IBlockDisplayReader;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z"},
            cancellable = true,
            remap = false)
    private void onRenderSmoothOrFlat(IBlockDisplayReader blockRenderView_1, IBakedModel bakedModel_1, BlockState blockState_1,
                                      BlockPos blockPos_1, MatrixStack matrixStack_1, IVertexBuilder vertexConsumer_1,
                                      boolean depthTest, Random random_1, long long_1, int int_1,
                                      IModelData modelData, CallbackInfoReturnable<Boolean> cir)
    {
        TesselateBlockEvent event = new TesselateBlockEvent(blockState_1);
        EventManager.fire(event);

        if(event.isCancelled())
        {
            cir.cancel();
            return;
        }

        if(!depthTest)
            return;

        ShouldDrawSideEvent event2 = new ShouldDrawSideEvent(blockState_1);
        EventManager.fire(event2);
        if(!Boolean.TRUE.equals(event2.isRendered()))
            return;

        renderModelSmooth(blockRenderView_1, bakedModel_1, blockState_1, blockPos_1,
                matrixStack_1, vertexConsumer_1, false, random_1, long_1, int_1, modelData);
    }

    @Shadow(remap = false)
    public boolean renderModelSmooth(IBlockDisplayReader worldIn, IBakedModel modelIn, BlockState stateIn,
                                     BlockPos posIn, MatrixStack matrixStackIn, IVertexBuilder buffer,
                                     boolean checkSides, Random randomIn, long rand, int combinedOverlayIn, IModelData modelData)

    {
        return false;
    }


}
