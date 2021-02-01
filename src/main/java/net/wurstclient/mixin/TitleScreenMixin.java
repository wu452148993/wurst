/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.wurstclient.WurstClient;
import net.wurstclient.altmanager.screens.AltManagerScreen;

@Mixin(MainMenuScreen.class)
public abstract class TitleScreenMixin extends Screen
{
	private TitleScreenMixin(WurstClient wurst, ITextComponent text_1)
	{
		super(text_1);
	}
	
	@Inject(at = {@At("RETURN")}, method = {"addSingleplayerMultiplayerButtons(II)V"})
	private void onInitWidgetsNormal(int y, int spacingY, CallbackInfo ci)
	{
		if(!WurstClient.INSTANCE.isEnabled())
			return;
		
		addButton(new Button(width / 2 + 2, y + spacingY * 2, 98, 20,
			new StringTextComponent("Alt Manager"),
			b -> minecraft.displayGuiScreen(new AltManagerScreen(this,
				WurstClient.INSTANCE.getAltManager()))));
		
		for(Widget button : buttons)
		{
			if(!button.getMessage().getString()
				.equals(I18n.format("menu.online")))
				continue;
			
			button.setWidth(98);
		}
	}
}
