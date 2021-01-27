/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.clickgui.screens;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.text.StringTextComponent;
import net.wurstclient.util.ListWidget;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.wurstclient.settings.ItemListSetting;

public final class EditItemListScreen extends Screen
{
	private final Screen prevScreen;
	private final ItemListSetting itemList;
	
	private ListGui listGui;
	private TextFieldWidget itemNameField;
	private Button addButton;
	private Button removeButton;
	private Button doneButton;
	
	private Item itemToAdd;
	
	public EditItemListScreen(Screen prevScreen, ItemListSetting itemList)
	{
		super(new StringTextComponent(""));
		this.prevScreen = prevScreen;
		this.itemList = itemList;
	}
	
	@Override
	public boolean isPauseScreen()
	{
		return false;
	}
	
	@Override
	public void init()
	{
		listGui = new ListGui(minecraft, this, itemList.getItemNames());
		
		itemNameField = new TextFieldWidget(minecraft.fontRenderer,
			width / 2 - 152, height - 55, 150, 18, new StringTextComponent(""));
		children.add(itemNameField);
		
		addButton(addButton = new Button(width / 2 - 2, height - 56, 30,
			20, new StringTextComponent("Add"), b -> {
				itemList.add(itemToAdd);
				itemNameField.setText("");
			}));
		
		addButton(removeButton = new Button(width / 2 + 52, height - 56,
			100, 20, new StringTextComponent("Remove Selected"),
			b -> itemList.remove(listGui.selected)));
		
		addButton(new Button(width - 108, 8, 100, 20,
			new StringTextComponent("Reset to Defaults"),
			b -> minecraft.displayGuiScreen(new ConfirmScreen(b2 -> {
				if(b2)
					itemList.resetToDefaults();
				minecraft.displayGuiScreen(EditItemListScreen.this);
			}, new StringTextComponent("Reset to Defaults"),
				new StringTextComponent("Are you sure?")))));
		
		addButton(
			doneButton = new Button(width / 2 - 100, height - 28, 200, 20,
				new StringTextComponent("Done"), b -> minecraft.displayGuiScreen(prevScreen)));
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		boolean childClicked = super.mouseClicked(mouseX, mouseY, mouseButton);
		
		itemNameField.mouseClicked(mouseX, mouseY, mouseButton);
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
			addButton.onPress();
		else if(keyCode == GLFW.GLFW_KEY_DELETE)
			removeButton.onPress();
		else if(keyCode == GLFW.GLFW_KEY_ESCAPE)
			doneButton.onPress();
		
		return super.keyPressed(keyCode, scanCode, int_3);
	}
	
	@Override
	public void tick()
	{
		itemNameField.tick();
		
		itemToAdd = Registry.ITEM.getOrDefault(getItemIDFromField());
		addButton.active = itemToAdd != null;
		
		removeButton.active =
			listGui.selected >= 0 && listGui.selected < listGui.list.size();
	}
	
	private ResourceLocation getItemIDFromField()
	{
		try
		{
			return new ResourceLocation(itemNameField.getText().toLowerCase());
			
		}catch(ResourceLocationException e)
		{
			return null;
		}
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY,
		float partialTicks)
	{
		renderBackground(matrixStack);
		listGui.render(matrixStack, mouseX, mouseY, partialTicks);
		
		drawCenteredString(matrixStack, minecraft.fontRenderer,
			itemList.getName() + " (" + listGui.getItemCount() + ")", width / 2,
			12, 0xffffff);
		
		itemNameField.render(matrixStack, mouseX, mouseY, partialTicks);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		
		GL11.glPushMatrix();
		GL11.glTranslated(-64 + width / 2 - 152, 0, 0);
		
		if(itemNameField.getText().isEmpty() && !itemNameField.isFocused())
		{
			GL11.glPushMatrix();
			GL11.glTranslated(0, 0, 300);
			drawString(matrixStack, minecraft.fontRenderer,
				"item name or ID", 68, height - 50, 0x808080);
			GL11.glPopMatrix();
		}
		
		fill(matrixStack, 48, height - 56, 64, height - 36, 0xffa0a0a0);
		fill(matrixStack, 49, height - 55, 64, height - 37, 0xff000000);
		fill(matrixStack, 214, height - 56, 244, height - 55, 0xffa0a0a0);
		fill(matrixStack, 214, height - 37, 244, height - 36, 0xffa0a0a0);
		fill(matrixStack, 244, height - 56, 246, height - 36, 0xffa0a0a0);
		fill(matrixStack, 214, height - 55, 243, height - 52, 0xff000000);
		fill(matrixStack, 214, height - 40, 243, height - 37, 0xff000000);
		fill(matrixStack, 215, height - 55, 216, height - 37, 0xff000000);
		fill(matrixStack, 242, height - 55, 245, height - 37, 0xff000000);
		listGui.renderIconAndGetName(matrixStack, new ItemStack(itemToAdd), 52,
			height - 52, false);
		
		GL11.glPopMatrix();
	}

	private static class ListGui extends ListWidget
	{
		private final Minecraft mc;
		private final List<String> list;
		private int selected = -1;
		
		public ListGui(Minecraft mc, EditItemListScreen screen,
			List<String> list)
		{
			super(mc, screen.width, screen.height, 32, screen.height - 64, 30);
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
			String name = list.get(index);
			Item item = Registry.ITEM.getOrDefault(new ResourceLocation(name));
			ItemStack stack = new ItemStack(item);
			FontRenderer fr = mc.fontRenderer;
			
			String displayName =
				renderIconAndGetName(matrixStack, stack, x + 1, y + 1, true);
			fr.drawString(matrixStack, displayName, x + 28, y, 0xf0f0f0);
			fr.drawString(matrixStack, name, x + 28, y + 9, 0xa0a0a0);
			fr.drawString(matrixStack, "ID: " + Registry.ITEM.getKey(item).toString(),
				x + 28, y + 18, 0xa0a0a0);
		}
		
		private String renderIconAndGetName(MatrixStack matrixStack,
											ItemStack stack, int x, int y, boolean large)
		{
			if(stack.isEmpty())
			{
				GL11.glPushMatrix();
				GL11.glTranslated(x, y, 0);
				if(large)
					GL11.glScaled(1.5, 1.5, 1.5);
				else
					GL11.glScaled(0.75, 0.75, 0.75);

				RenderHelper.enableStandardItemLighting();
				mc.getItemRenderer().renderItemAndEffectIntoGUI(
					new ItemStack(Blocks.GRASS_BLOCK), 0, 0);
				RenderHelper.disableStandardItemLighting();
				GL11.glPopMatrix();
				
				GL11.glPushMatrix();
				GL11.glTranslated(x, y, 0);
				if(large)
					GL11.glScaled(2, 2, 2);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				FontRenderer fr = mc.fontRenderer;
				fr.drawStringWithShadow(matrixStack, "?", 3, 2, 0xf0f0f0);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glPopMatrix();
				
				return "\u00a7ounknown item\u00a7r";
				
			}else
			{
				GL11.glPushMatrix();
				GL11.glTranslated(x, y, 0);
				if(large)
					GL11.glScaled(1.5, 1.5, 1.5);
				else
					GL11.glScaled(0.75, 0.75, 0.75);

				RenderHelper.enableStandardItemLighting();
				mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
				RenderHelper.disableStandardItemLighting();
				
				GL11.glPopMatrix();
				
				return stack.getDisplayName().getString();
			}
		}
	}
}
