/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixinterface;

import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public interface IClientPlayerInteractionManager
{
	public float getCurrentBreakingProgress();
	
	public void setBreakingBlock(boolean breakingBlock);
	
	public ItemStack windowClick_PICKUP(int slot);
	
	public ItemStack windowClick_QUICK_MOVE(int slot);
	
	public ItemStack windowClick_THROW(int slot);
	
	public void rightClickItem();
	
	public void rightClickBlock(BlockPos pos, Direction side, Vector3d hitVec);
	
	public void sendPlayerActionC2SPacket(CPlayerDiggingPacket.Action action,
										  BlockPos blockPos, Direction direction);
	
	public void setBlockHitDelay(int delay);
}
