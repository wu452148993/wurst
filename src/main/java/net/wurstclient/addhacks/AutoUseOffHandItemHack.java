/*
 * Copyright (c) 2014-2022 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.addhacks;

import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.util.ChatUtils;

@SearchTags({"Eat"})
public final class AutoUseOffHandItemHack extends Hack implements UpdateListener {

    private final CheckboxSetting onHungry = new CheckboxSetting(
            "onHungry", "Eat you offHand items when you are hungry.", true);

    public AutoUseOffHandItemHack() {
        super("AutoUseOffHand", "Auto Use your OffHand Item");
        setCategory(Category.COMBAT);
        addSetting(onHungry);
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
        if(MC.player.inventory.offHandInventory.isEmpty()) {
            return;
        }

        if (onHungry.isChecked() && MC.player.canEat(false)) {
            MC.gameSettings.keyBindUseItem.setPressed(true);
        }
    }
}
