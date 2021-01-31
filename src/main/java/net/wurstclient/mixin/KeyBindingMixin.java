/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.wurstclient.WurstClient;
import net.wurstclient.mixinterface.IKeyBinding;

@Mixin(KeyBinding.class)
public class KeyBindingMixin implements IKeyBinding
{
	@Shadow
	private InputMappings.Input keyCode;
	
	@Override
	public boolean isActallyPressed()
	{
		long handle = WurstClient.MC.getMainWindow().getHandle();
		int code = keyCode.getKeyCode();
		return InputMappings.isKeyDown(handle, code);
	}
}
