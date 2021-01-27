/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import net.minecraft.client.KeyboardListener;
import net.wurstclient.event.EventManager;
import net.wurstclient.events.KeyPressListener.KeyPressEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardListener.class)
public class KeyboardMixin
{
	@Inject(at = @At("HEAD"), method = "onKeyEvent(JIIII)V")
	private void onOnKey(long windowHandle, int keyCode, int scanCode,
		int action, int modifiers, CallbackInfo ci)
	{
		KeyPressEvent event =
			new KeyPressEvent(keyCode, scanCode, action, modifiers);
		
		EventManager.fire(event);
	}
}
