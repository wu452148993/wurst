/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.IsPlayerInWaterListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"FlyHack", "fly hack", "flying"})
public final class FlightHack extends Hack
	implements UpdateListener, IsPlayerInWaterListener
{
	public final SliderSetting speed =
		new SliderSetting("Speed", 1, 0.05, 5, 0.05, ValueDisplay.DECIMAL);
	
	public FlightHack()
	{
		super("Flight",
			"Allows you to you fly.\n\n" + "\u00a7c\u00a7lWARNING:\u00a7r"
				+ " You will take fall damage if you don't use NoFall.");
		setCategory(Category.MOVEMENT);
		addSetting(speed);
	}
	
	@Override
	public void onEnable()
	{
		WURST.getHax().jetpackHack.setEnabled(false);
		WURST.getHax().glideHack.setEnabled(false);

		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(IsPlayerInWaterListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(IsPlayerInWaterListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		ClientPlayerEntity player = MC.player;
		
		player.abilities.isFlying = false;
		player.jumpMovementFactor = speed.getValueF();
		
		player.setVelocity(0, 0, 0);
		Vector3d velcity = player.getMotion();
		
		if(MC.gameSettings.keyBindJump.isKeyDown())
			player.setMotion(velcity.add(0, speed.getValue(), 0));
		
		if(MC.gameSettings.keyBindSneak.isKeyDown())
			player.setMotion(velcity.subtract(0, speed.getValue(), 0));
	}
	
	@Override
	public void onIsPlayerInWater(IsPlayerInWaterEvent event)
	{
		event.setInWater(false);
	}
}
