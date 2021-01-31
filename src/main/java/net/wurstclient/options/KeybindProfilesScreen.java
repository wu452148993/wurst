/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.options;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Util;
import net.wurstclient.WurstClient;
import net.wurstclient.util.ListWidget;
import net.wurstclient.util.json.JsonException;

public final class KeybindProfilesScreen extends Screen
{
	private final Screen prevScreen;
	
	private ListGui listGui;
	private Button loadButton;
	
	public KeybindProfilesScreen(Screen prevScreen)
	{
		super(new StringTextComponent(""));
		this.prevScreen = prevScreen;
	}
	
	@Override
	public void init()
	{
		listGui = new ListGui(minecraft, this,
			WurstClient.INSTANCE.getKeybinds().listProfiles());
		
		addButton(new Button(8, 8, 100, 20,
			new StringTextComponent("Open Folder"), b -> openFolder()));
		
		addButton(new Button(width / 2 - 154, height - 48, 100, 20,
			new StringTextComponent("New Profile"), b -> minecraft.displayGuiScreen(
				new EnterProfileNameScreen(this, this::newProfile))));
		
		loadButton = addButton(new Button(width / 2 - 50, height - 48,
			100, 20, new StringTextComponent("Load"), b -> loadSelected()));
		
		addButton(new Button(width / 2 + 54, height - 48, 100, 20,
			new StringTextComponent("Cancel"), b -> openPrevScreen()));
	}
	
	private void openFolder()
	{
		Util.getOSType().openFile(
			WurstClient.INSTANCE.getKeybinds().getProfilesFolder().toFile());
	}
	
	private void newProfile(String name)
	{
		if(!name.endsWith(".json"))
			name += ".json";
		
		try
		{
			WurstClient.INSTANCE.getKeybinds().saveProfile(name);
			
		}catch(IOException | JsonException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private void loadSelected()
	{
		if(listGui.selected < 0 || listGui.selected >= listGui.list.size())
		{
			openPrevScreen();
			return;
		}
		
		Path path = listGui.list.get(listGui.selected);
		String fileName = "" + path.getFileName();
		
		try
		{
			WurstClient.INSTANCE.getKeybinds().loadProfile(fileName);
			openPrevScreen();
			
		}catch(IOException | JsonException e)
		{
			e.printStackTrace();
			return;
		}
	}
	
	private void openPrevScreen()
	{
		minecraft.displayGuiScreen(prevScreen);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		boolean childClicked = super.mouseClicked(mouseX, mouseY, mouseButton);
		
		listGui.mouseClicked(mouseX, mouseY, mouseButton);
		
		if(!childClicked && (mouseX < (width - 220) / 2
			|| mouseX > width / 2 + 129 || mouseY < 32 || mouseY > height - 64))
			listGui.selected = -1;
		
		return childClicked;
	}
	
	@Override
	public boolean mouseDragged(double double_1, double double_2, int int_1,
		double double_3, double double_4)
	{
		listGui.mouseDragged(double_1, double_2, int_1, double_3, double_4);
		return super.mouseDragged(double_1, double_2, int_1, double_3,
			double_4);
	}
	
	@Override
	public boolean mouseReleased(double double_1, double double_2, int int_1)
	{
		listGui.mouseReleased(double_1, double_2, int_1);
		return super.mouseReleased(double_1, double_2, int_1);
	}
	
	@Override
	public boolean mouseScrolled(double double_1, double double_2,
		double double_3)
	{
		listGui.mouseScrolled(double_1, double_2, double_3);
		return super.mouseScrolled(double_1, double_2, double_3);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int int_3)
	{
		if(keyCode == GLFW.GLFW_KEY_ENTER)
			loadSelected();
		else if(keyCode == GLFW.GLFW_KEY_ESCAPE)
			openPrevScreen();
		
		return super.keyPressed(keyCode, scanCode, int_3);
	}
	
	@Override
	public void tick()
	{
		loadButton.active =
			listGui.selected >= 0 && listGui.selected < listGui.list.size();
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY,
		float partialTicks)
	{
		renderBackground(matrixStack);
		listGui.render(matrixStack, mouseX, mouseY, partialTicks);
		
		drawCenteredString(matrixStack, minecraft.fontRenderer, "Keybind Profiles",
			width / 2, 12, 0xffffff);
		
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		
		if(loadButton.isHovered() && !loadButton.active)
			func_243308_b(matrixStack,
				Arrays.asList(new StringTextComponent("You must first select a file.")),
				mouseX, mouseY);
	}
	
	private static class ListGui extends ListWidget
	{
		private final Minecraft mc;
		private final List<Path> list;
		private int selected = -1;
		
		public ListGui(Minecraft mc, KeybindProfilesScreen screen,
			ArrayList<Path> list)
		{
			super(mc, screen.width, screen.height, 36, screen.height - 64, 20);
			this.mc = mc;
			this.list = list;
		}
		
		@Override
		protected int getItemCount()
		{
			return list.size();
		}
		
		@Override
		protected boolean selectItem(int index, int int_2, double var3,
			double var4)
		{
			if(index >= 0 && index < list.size())
				selected = index;
			
			return true;
		}
		
		@Override
		protected boolean isSelectedItem(int index)
		{
			return index == selected;
		}
		
		@Override
		protected void renderBackground()
		{
			
		}
		
		@Override
		protected void renderItem(MatrixStack matrixStack, int index, int x,
								  int y, int var4, int var5, int var6, float partialTicks)
		{
			FontRenderer fr = mc.fontRenderer;
			
			Path path = list.get(index);
			fr.drawString(matrixStack, "" + path.getFileName(), x + 28, y, 0xf0f0f0);
			fr.drawString(matrixStack,
				"" + client.gameDir.toPath().relativize(path), x + 28,
				y + 9, 0xa0a0a0);
		}
	}
}
