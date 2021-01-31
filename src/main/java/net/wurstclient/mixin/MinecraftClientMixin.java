/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.IWindowEventListener;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.util.Session;
import net.minecraft.util.concurrent.RecursiveEventLoop;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.Entity;
import net.wurstclient.WurstClient;
import net.wurstclient.event.EventManager;
import net.wurstclient.events.LeftClickListener.LeftClickEvent;
import net.wurstclient.events.RightClickListener.RightClickEvent;
import net.wurstclient.mixinterface.IClientPlayerEntity;
import net.wurstclient.mixinterface.IClientPlayerInteractionManager;
import net.wurstclient.mixinterface.IMinecraftClient;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin
	extends RecursiveEventLoop<Runnable> implements ISnooperInfo,
		IWindowEventListener, AutoCloseable, IMinecraftClient
{
	@Shadow
	private int rightClickDelayTimer;
	@Shadow
	private PlayerController playerController;
	@Shadow
	private ClientPlayerEntity player;
	@Shadow
	private Session session;
	
	private Session wurstSession;
	
	private MinecraftClientMixin(WurstClient wurst, String string_1)
	{
		super(string_1);
	}
	
	@Inject(at = {@At(value = "FIELD",
		target = "Lnet/minecraft/client/Minecraft;objectMouseOver:Lnet/minecraft/util/math/RayTraceResult;",
		ordinal = 0)}, method = {"clickMouse()V"}, cancellable = true)
	private void onDoAttack(CallbackInfo ci)
	{
		LeftClickEvent event = new LeftClickEvent();
		EventManager.fire(event);
		
		if(event.isCancelled())
			ci.cancel();
	}
	
	@Inject(at = {@At(value = "FIELD",
		target = "Lnet/minecraft/client/Minecraft;rightClickDelayTimer:I",
		ordinal = 0)}, method = {"rightClickMouse()V"}, cancellable = true)
	private void onDoItemUse(CallbackInfo ci)
	{
		RightClickEvent event = new RightClickEvent();
		EventManager.fire(event);
		
		if(event.isCancelled())
			ci.cancel();
	}
	
	@Inject(at = {@At("HEAD")}, method = {"middleClickMouse()V"})
	private void onDoItemPick(CallbackInfo ci)
	{
		if(!WurstClient.INSTANCE.isEnabled())
			return;

		RayTraceResult hitResult = WurstClient.MC.objectMouseOver;
		if(hitResult == null || hitResult.getType() != RayTraceResult.Type.ENTITY)
			return;
		
		Entity entity = ((EntityRayTraceResult)hitResult).getEntity();
		WurstClient.INSTANCE.getFriends().middleClick(entity);
	}
	
	@Inject(at = {@At("HEAD")},
		method = {"getSession()Lnet/minecraft/util/Session;"},
		cancellable = true)
	private void onGetSession(CallbackInfoReturnable<Session> cir)
	{
		if(wurstSession == null)
			return;
		
		cir.setReturnValue(wurstSession);
	}
	
	@Redirect(at = @At(value = "FIELD",
		target = "Lnet/minecraft/client/Minecraft;session:Lnet/minecraft/util/Session;",
		opcode = Opcodes.GETFIELD,
		ordinal = 0),
		method = {
			"getProfileProperties()Lcom/mojang/authlib/properties/PropertyMap;"})
	private Session getSessionForSessionProperties(Minecraft mc)
	{
		if(wurstSession != null)
			return wurstSession;
		else
			return session;
	}
	
	@Override
	public void rightClick()
	{
		rightClickMouse();
	}
	
	@Override
	public int getItemUseCooldown()
	{
		return rightClickDelayTimer;
	}
	
	@Override
	public void setItemUseCooldown(int itemUseCooldown)
	{
		this.rightClickDelayTimer = itemUseCooldown;
	}
	
	@Override
	public IClientPlayerEntity getPlayer()
	{
		return (IClientPlayerEntity)player;
	}
	
	@Override
	public IClientPlayerInteractionManager getInteractionManager()
	{
		return (IClientPlayerInteractionManager)playerController;
	}
	
	@Override
	public void setSession(Session session)
	{
		wurstSession = session;
	}
	
	@Shadow
	private void rightClickMouse()
	{
		
	}
}
