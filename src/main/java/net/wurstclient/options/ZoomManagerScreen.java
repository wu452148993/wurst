/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.options;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.StringTextComponent;
import net.wurstclient.WurstClient;
import net.wurstclient.other_features.ZoomOtf;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;

public class ZoomManagerScreen extends Screen implements PressAKeyCallback
{
	private Screen prevScreen;
	private Button keyButton;
	private Button scrollButton;
	
	public ZoomManagerScreen(Screen par1GuiScreen)
	{
		super(new StringTextComponent(""));
		prevScreen = par1GuiScreen;
	}
	
	@Override
	public void init()
	{
		ZoomOtf zoom = WurstClient.INSTANCE.getOtfs().zoomOtf;
		SliderSetting level = zoom.getLevelSetting();
		CheckboxSetting scroll = zoom.getScrollSetting();
		String zoomKeyName = WurstClient.INSTANCE.getZoomKey()
			.getTranslationKey().replace("key.keyboard.", "");
		
		addButton(new Button(width / 2 - 100, height / 4 + 144 - 16, 200,
			20, new StringTextComponent("Back"), b -> minecraft.displayGuiScreen(prevScreen)));
		
		addButton(
			keyButton = new Button(width / 2 - 79, height / 4 + 24 - 16,
				158, 20, new StringTextComponent("Zoom Key: " + zoomKeyName),
				b -> minecraft.displayGuiScreen(new PressAKeyScreen(this))));
		
		addButton(new Button(width / 2 - 79, height / 4 + 72 - 16, 50, 20,
			new StringTextComponent("More"), b -> level.increaseValue()));
		
		addButton(new Button(width / 2 - 25, height / 4 + 72 - 16, 50, 20,
			new StringTextComponent("Less"), b -> level.decreaseValue()));
		
		addButton(new Button(width / 2 + 29, height / 4 + 72 - 16, 50, 20,
			new StringTextComponent("Default"),
			b -> level.setValue(level.getDefaultValue())));
		
		addButton(scrollButton =
			new Button(width / 2 - 79, height / 4 + 96 - 16, 158, 20,
				new StringTextComponent(
					"Use Mouse Wheel: " + onOrOff(scroll.isChecked())),
				b -> toggleScroll()));
	}
	
	private void toggleScroll()
	{
		ZoomOtf zoom = WurstClient.INSTANCE.getOtfs().zoomOtf;
		CheckboxSetting scroll = zoom.getScrollSetting();
		
		scroll.setChecked(!scroll.isChecked());
		scrollButton.setMessage(
			new StringTextComponent("Use Mouse Wheel: " + onOrOff(scroll.isChecked())));
	}
	
	private String onOrOff(boolean on)
	{
		return on ? "ON" : "OFF";
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY,
					   float partialTicks)
	{
		ZoomOtf zoom = WurstClient.INSTANCE.getOtfs().zoomOtf;
		SliderSetting level = zoom.getLevelSetting();
		
		renderBackground(matrixStack);
		drawCenteredString(matrixStack, font, "Zoom Manager", width / 2,
			40, 0xffffff);
		drawString(matrixStack, font,
			"Zoom Level: " + level.getValueString(), width / 2 - 75,
			height / 4 + 44, 0xcccccc);
		
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void setKey(String key)
	{
		WurstClient.INSTANCE.getZoomKey()
			.bind(InputMappings.getInputByName(key));
		minecraft.gameSettings.saveOptions();
		KeyBinding.resetKeyBindingArrayAndHash();
		keyButton.setMessage(new StringTextComponent("Zoom Key: " + key));
	}
}
