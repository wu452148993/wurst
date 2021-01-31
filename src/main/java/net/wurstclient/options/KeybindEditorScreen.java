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
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.wurstclient.WurstClient;

public final class KeybindEditorScreen extends Screen
	implements PressAKeyCallback
{
	private final Screen prevScreen;
	
	private String key;
	private final String oldKey;
	private final String oldCommands;
	
	private TextFieldWidget commandField;
	
	public KeybindEditorScreen(Screen prevScreen)
	{
		super(new StringTextComponent(""));
		this.prevScreen = prevScreen;
		
		key = "NONE";
		oldKey = null;
		oldCommands = null;
	}
	
	public KeybindEditorScreen(Screen prevScreen, String key, String commands)
	{
		super(new StringTextComponent(""));
		this.prevScreen = prevScreen;
		
		this.key = key;
		oldKey = key;
		oldCommands = commands;
	}
	
	@Override
	public void init()
	{
		addButton(new Button(width / 2 - 100, 60, 200, 20,
			new StringTextComponent("Change Key"),
			b -> minecraft.displayGuiScreen(new PressAKeyScreen(this))));
		
		addButton(new Button(width / 2 - 100, height / 4 + 72, 200, 20,
			new StringTextComponent("Save"), b -> save()));
		
		addButton(new Button(width / 2 - 100, height / 4 + 96, 200, 20,
			new StringTextComponent("Cancel"), b -> minecraft.displayGuiScreen(prevScreen)));
		
		commandField = new TextFieldWidget(font, width / 2 - 100, 100,
			200, 20, new StringTextComponent(""));
		commandField.setMaxStringLength(65536);
		children.add(commandField);
		setFocusedDefault(commandField);
		commandField.setFocused2(true);
		
		if(oldCommands != null)
			commandField.setText(oldCommands);
	}
	
	private void save()
	{
		if(oldKey != null)
			WurstClient.INSTANCE.getKeybinds().remove(oldKey);
		
		WurstClient.INSTANCE.getKeybinds().add(key, commandField.getText());
		minecraft.displayGuiScreen(prevScreen);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		commandField.mouseClicked(mouseX, mouseY, mouseButton);
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void tick()
	{
		commandField.tick();
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY,
					   float partialTicks)
	{
		renderBackground(matrixStack);
		
		drawCenteredString(matrixStack, font,
			(oldKey != null ? "Edit" : "Add") + " Keybind", width / 2, 20,
			0xffffff);

		drawString(matrixStack, font,
			"Key: " + key.replace("key.keyboard.", ""), width / 2 - 100, 47,
			0xa0a0a0);
		drawString(matrixStack, font,
			"Commands (separated by ';')", width / 2 - 100, 87, 0xa0a0a0);
		
		commandField.render(matrixStack, mouseX, mouseY, partialTicks);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void setKey(String key)
	{
		this.key = key;
	}
}
