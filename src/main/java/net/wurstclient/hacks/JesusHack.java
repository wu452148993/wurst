/*
 * Copyright (c) 2014-2022 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.fluid.Fluid;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;

@SearchTags({"WaterWalking", "water walking"})
public final class JesusHack extends Hack
        implements UpdateListener {
    private final CheckboxSetting bypass =
            new CheckboxSetting("NoCheat+ bypass",
                    "Bypasses NoCheat+ but slows down your movement.", false);

    private int tickTimer = 10;
    private int packetTimer = 0;

    public JesusHack() {
        super("Jesus", "");
        setCategory(Category.MOVEMENT);
        addSetting(bypass);
    }

    @Override
    public void onEnable() {
        EVENTS.add(UpdateListener.class, this);
    }

    @Override
    public void onDisable() {
        EVENTS.remove(UpdateListener.class, this);
    }

    @Override
    public void onUpdate() {

    }

}
