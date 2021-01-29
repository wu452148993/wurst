/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.event.EventManager;
import net.wurstclient.events.SetOpaqueCubeListener.SetOpaqueCubeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VisGraph.class)
public class ChunkOcclusionGraphBuilderMixin
{
	@Inject(at = {@At("HEAD")},
		method = {"setOpaqueCube(Lnet/minecraft/util/math/BlockPos;)V"},
		cancellable = true)
	private void onMarkClosed(BlockPos pos, CallbackInfo ci)
	{
		SetOpaqueCubeEvent event = new SetOpaqueCubeEvent();
		EventManager.fire(event);
		
		if(event.isCancelled())
			ci.cancel();
	}
}
