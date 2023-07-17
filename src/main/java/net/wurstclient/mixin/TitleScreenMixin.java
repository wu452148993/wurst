/*
 * Copyright (c) 2014-2022 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.wurstclient.WurstClient;
import net.wurstclient.altmanager.screens.AltManagerScreen;
import net.wurstclient.mixinterface.IScreen;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen
{
	private ClickableWidget realmsButton = null;
	private ButtonWidget altsButton;

	private final Identifier CMM_BUTTON_TEXTURES = new Identifier("wurst", "cmmbutton.png");

	private TitleScreenMixin(WurstClient wurst, Text text_1)
	{
		super(text_1);
	}
	
	@Inject(at = {@At("RETURN")}, method = {"init()V"})
	private void onInitWidgetsNormal(CallbackInfo ci)
	{
		if(!WurstClient.INSTANCE.isEnabled())
			return;
		
		for(Drawable d : ((IScreen)this).getButtons())
		{
			if(!(d instanceof ClickableWidget))
				continue;
			
			ClickableWidget button = (ClickableWidget)d;
			if(!button.getMessage().getString()
				.equals(I18n.translate("menu.online")))
				continue;
			
			realmsButton = button;
			break;
		}
		
		if(realmsButton == null)
			throw new IllegalStateException("Couldn't find realms button!");
		
		// make Realms button smaller
		realmsButton.setWidth(98);
		
		// add AltManager button
		addDrawableChild(altsButton = new TexturedButtonWidget(width / 2 + 104,
			realmsButton.y, 20, 20, 0, 0, 20, CMM_BUTTON_TEXTURES, 20, 40,
			b -> client.setScreen(new AltManagerScreen(this,
				WurstClient.INSTANCE.getAltManager())), new LiteralText("Alt Manager")));
	}
	
	@Inject(at = {@At("RETURN")}, method = {"tick()V"})
	private void onTick(CallbackInfo ci)
	{
		if(realmsButton == null || altsButton == null)
			return;
			
		// adjust AltManager button if Realms button has been moved
		// happens when ModMenu is installed
		altsButton.y = realmsButton.y;
	}
}
