/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.wurstclient.WurstClient;
import net.wurstclient.options.WurstOptionsScreen;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IngameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen
{
	private static final ResourceLocation wurstTexture =
		new ResourceLocation("wurst", "wurst_128.png");
	
	private Button wurstOptionsButton;
	
	private GameMenuScreenMixin(WurstClient wurst, ITextComponent text_1)
	{
		super(text_1);
	}
	
	@Inject(at = {@At("TAIL")}, method = {"addButtons()V"})
	private void onInitWidgets(CallbackInfo ci)
	{
		if(!WurstClient.INSTANCE.isEnabled())
			return;
		
		addWurstOptionsButton();
		removeFeedbackAndBugReportButtons();
	}
	
	private void addWurstOptionsButton()
	{
		wurstOptionsButton = new Button(width / 2 - 102, height / 4 + 56,
			204, 20, new StringTextComponent("            Options"),
			b -> openWurstOptions());
		
		addButton(wurstOptionsButton);
	}
	
	private void openWurstOptions()
	{
		minecraft.displayGuiScreen(new WurstOptionsScreen(this));
	}
	
	private void removeFeedbackAndBugReportButtons()
	{
		buttons.removeIf(this::isFeedbackOrBugReportButton);
		children.removeIf(this::isFeedbackOrBugReportButton);
	}
	
	private boolean isFeedbackOrBugReportButton(IGuiEventListener element)
	{
		if(element == null || !(element instanceof Widget))
			return false;

		Widget button = (Widget)element;
		String message = button.getMessage().getString();
		
		return message != null
			&& (message.equals(I18n.format("menu.sendFeedback"))
				|| message.equals(I18n.format("menu.reportBugs")));
	}
	
	@Inject(at = {@At("TAIL")},
		method = {"render(Lcom/mojang/blaze3d/matrix/MatrixStack;IIF)V"})
	private void onRender(MatrixStack matrixStack, int mouseX, int mouseY,
						  float partialTicks, CallbackInfo ci)
	{
		if(!WurstClient.INSTANCE.isEnabled())
			return;

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1, 1, 1, 1);
		
		minecraft.getTextureManager().bindTexture(wurstTexture);
		
		int x = wurstOptionsButton.x + 34;
		int y = wurstOptionsButton.y + 2;
		int w = 63;
		int h = 16;
		int fw = 63;
		int fh = 16;
		float u = 0;
		float v = 0;
		blit(matrixStack, x, y, u, v, w, h, fw, fh);

		GL11.glPopAttrib();
	}
}
