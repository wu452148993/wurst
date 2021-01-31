/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.util;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.EntityDataManager;
import net.wurstclient.WurstClient;

public class FakePlayerEntity extends RemoteClientPlayerEntity
{
	private final ClientPlayerEntity player = WurstClient.MC.player;
	private final ClientWorld world = WurstClient.MC.world;
	
	public FakePlayerEntity()
	{
		super(WurstClient.MC.world, WurstClient.MC.player.getGameProfile());
		copyLocationAndAnglesFrom(player);
		
		copyInventory();
		copyPlayerModel(player, this);
		copyRotation();
		resetCapeMovement();
		
		spawn();
	}
	
	private void copyInventory()
	{
		inventory.copyInventory(player.inventory);
	}
	
	private void copyPlayerModel(Entity from, Entity to)
	{
		EntityDataManager fromTracker = from.getDataManager();
		EntityDataManager toTracker = to.getDataManager();
		Byte playerModel = fromTracker.get(PlayerEntity.PLAYER_MODEL_FLAG);
		toTracker.set(PlayerEntity.PLAYER_MODEL_FLAG, playerModel);
	}
	
	private void copyRotation()
	{
		rotationYawHead = player.rotationYawHead;
		renderYawOffset = player.renderYawOffset;
	}
	
	private void resetCapeMovement()
	{
		chasingPosX = getPosX();
		chasingPosY = getPosY();
		chasingPosZ = getPosZ();
	}
	
	private void spawn()
	{
		world.addEntity(getEntityId(), this);
	}
	
	public void despawn()
	{
		removed = true;
	}
	
	public void resetPlayerPosition()
	{
		player.setLocationAndAngles(getPosX(), getPosY(), getPosZ(), rotationYaw, rotationPitch);
	}
}
