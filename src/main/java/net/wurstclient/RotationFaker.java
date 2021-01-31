/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.wurstclient.events.PostMotionListener;
import net.wurstclient.events.PreMotionListener;
import net.wurstclient.util.RotationUtils;

public final class RotationFaker
	implements PreMotionListener, PostMotionListener
{
	private boolean fakeRotation;
	private float serverYaw;
	private float serverPitch;
	private float realYaw;
	private float realPitch;
	
	@Override
	public void onPreMotion()
	{
		if(!fakeRotation)
			return;
		
		ClientPlayerEntity player = WurstClient.MC.player;
		realYaw = player.rotationYaw;
		realPitch = player.rotationPitch;
		player.rotationYaw = serverYaw;
		player.rotationPitch = serverPitch;
	}
	
	@Override
	public void onPostMotion()
	{
		if(!fakeRotation)
			return;
		
		ClientPlayerEntity player = WurstClient.MC.player;
		player.rotationYaw = realYaw;
		player.rotationPitch = realPitch;
		fakeRotation = false;
	}
	
	public void faceVectorPacket(Vector3d vec)
	{
		RotationUtils.Rotation rotations =
			RotationUtils.getNeededRotations(vec);
		
		fakeRotation = true;
		serverYaw = rotations.getYaw();
		serverPitch = rotations.getPitch();
	}
	
	public void faceVectorClient(Vector3d vec)
	{
		RotationUtils.Rotation rotations =
			RotationUtils.getNeededRotations(vec);
		
		WurstClient.MC.player.rotationYaw = rotations.getYaw();
		WurstClient.MC.player.rotationPitch = rotations.getPitch();
	}
	
	public void faceVectorClientIgnorePitch(Vector3d vec)
	{
		RotationUtils.Rotation rotations =
			RotationUtils.getNeededRotations(vec);
		
		WurstClient.MC.player.rotationYaw = rotations.getYaw();
		WurstClient.MC.player.rotationPitch = 0;
	}
	
	public float getServerYaw()
	{
		return fakeRotation ? serverYaw : WurstClient.MC.player.rotationYaw;
	}
	
	public float getServerPitch()
	{
		return fakeRotation ? serverPitch : WurstClient.MC.player.rotationPitch;
	}
}
