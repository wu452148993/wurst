/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.wurstclient.hacks.FullbrightHack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.world.ClientWorld;
import net.wurstclient.WurstClient;
import net.wurstclient.event.EventManager;
import net.wurstclient.events.ChatOutputListener.ChatOutputEvent;
import net.wurstclient.events.IsPlayerInWaterListener.IsPlayerInWaterEvent;
import net.wurstclient.events.KnockbackListener.KnockbackEvent;
import net.wurstclient.events.PlayerMoveListener.PlayerMoveEvent;
import net.wurstclient.events.PostMotionListener.PostMotionEvent;
import net.wurstclient.events.PreMotionListener.PreMotionEvent;
import net.wurstclient.events.UpdateListener.UpdateEvent;
import net.wurstclient.mixinterface.IClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity
	implements IClientPlayerEntity
{
	@Shadow
	private float lastReportedYaw;
	@Shadow
	private float lastReportedPitch;
	@Shadow
	private ClientPlayNetHandler connection;
	
	public ClientPlayerEntityMixin(WurstClient wurst, ClientWorld clientWorld_1,
		GameProfile gameProfile_1)
	{
		super(clientWorld_1, gameProfile_1);
	}
	
	@Inject(at = @At("HEAD"),
		method = "sendChatMessage(Ljava/lang/String;)V",
		cancellable = true)
	private void onSendChatMessage(String message, CallbackInfo ci)
	{
		ChatOutputEvent event = new ChatOutputEvent(message);
		EventManager.fire(event);
		
		if(event.isCancelled())
		{
			ci.cancel();
			return;
		}
		
		if(!event.isModified())
			return;

        CChatMessagePacket packet =
			new CChatMessagePacket(event.getMessage());
		connection.sendPacket(packet);
		ci.cancel();
	}
	
	@Inject(at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;tick()V",
		ordinal = 0), method = "tick()V")
	private void onTick(CallbackInfo ci)
	{
		EventManager.fire(UpdateEvent.INSTANCE);
	}
	
	@Redirect(at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;isHandActive()Z",
		ordinal = 0), method = "livingTick()V")
	private boolean wurstIsUsingItem(ClientPlayerEntity player)
	{
		if(WurstClient.INSTANCE.getHax().noSlowdownHack.isEnabled())
			return false;
		
		return player.isHandActive();
	}
	
	@Inject(at = {@At("HEAD")}, method = {"onUpdateWalkingPlayer()V"})
	private void onSendMovementPacketsHEAD(CallbackInfo ci)
	{
		EventManager.fire(PreMotionEvent.INSTANCE);
	}
	
	@Inject(at = {@At("TAIL")}, method = {"onUpdateWalkingPlayer()V"})
	private void onSendMovementPacketsTAIL(CallbackInfo ci)
	{
		EventManager.fire(PostMotionEvent.INSTANCE);
	}
	
	@Inject(at = {@At("HEAD")},
		method = {
			"move(Lnet/minecraft/entity/MoverType;Lnet/minecraft/util/math/vector/Vector3d;)V"})
	private void onMove(MoverType type, Vector3d offset, CallbackInfo ci)
	{
		PlayerMoveEvent event = new PlayerMoveEvent(this);
		EventManager.fire(event);
	}

	/*
	@Inject(at = {@At("HEAD")},
		method = {"isAutoJumpEnabled()Z"},
		cancellable = true)
	private void onIsAutoJumpEnabled(CallbackInfoReturnable<Boolean> cir)
	{
		if(!WurstClient.INSTANCE.getHax().stepHack.isAutoJumpAllowed())
			cir.setReturnValue(false);
	}*/
	
	@Override
	public void setVelocity(double x, double y, double z)
	{
		KnockbackEvent event = new KnockbackEvent(x, y, z);
		EventManager.fire(event);
		super.setVelocity(event.getX(), event.getY(), event.getZ());
	}
	
	@Override
	public boolean isInWater()
	{
		boolean inWater = super.isInWater();
		IsPlayerInWaterEvent event = new IsPlayerInWaterEvent(inWater);
		EventManager.fire(event);
		
		return event.isInWater();
	}
	
	@Override
	public boolean isTouchingWaterBypass()
	{
		return super.isInWater();
	}
	
	@Override
	protected float getJumpUpwardsMotion()
	{
		return super.getJumpUpwardsMotion()
			+ WurstClient.INSTANCE.getHax().highJumpHack
				.getAdditionalJumpMotion();
	}
	
	@Override
	public boolean isSecondaryUseActive()
	{
		return super.isSecondaryUseActive()/*
			|| WurstClient.INSTANCE.getHax().safeWalkHack.isEnabled()*/;
	}

	@Override
	public boolean isPotionActive(Effect effect)
	{
		FullbrightHack fullbright =
				WurstClient.INSTANCE.getHax().fullbrightHack;

		if(effect == Effects.NIGHT_VISION
				&& fullbright.isNightVisionActive())
			return true;

		return super.isPotionActive(effect);
	}


	@Override
	public void setNoClip(boolean noClip)
	{
		this.noClip = noClip;
	}
	
	@Override
	public float getLastYaw()
	{
		return lastReportedYaw;
	}
	
	@Override
	public float getLastPitch()
	{
		return lastReportedPitch;
	}
	
	@Override
	public void setMovementMultiplier(Vector3d movementMultiplier)
	{
		this.motionMultiplier = movementMultiplier;
	}
}
