/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"trigger bot", "auto attack"})
public final class TriggerBotHack extends Hack
        implements UpdateListener {

    private final CheckboxSetting filterPets =
            new CheckboxSetting("Filter pets",
                    "Won't attack tamed wolves,\n" + "tamed horses, etc.", false);

    private final CheckboxSetting filterPlayers = new CheckboxSetting(
            "Filter players", "Won't attack other players.", false);

    public TriggerBotHack() {
        super("TriggerBot", "");
        setCategory(Category.COMBAT);
        addSetting(filterPets);
        addSetting(filterPlayers);
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
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null && mc.player.getCooledAttackStrength(0) >= 1) {
            if (mc.objectMouseOver instanceof EntityRayTraceResult) {
                Entity entity = ((EntityRayTraceResult) mc.objectMouseOver).getEntity();
                if (isCorrectEntity(entity)) {
                    mc.playerController.attackEntity(mc.player, entity);
                }
            }
        }
    }

    private boolean isCorrectEntity(Entity entity) {
        if (filterPets.isChecked() && entity instanceof TameableEntity && ((TameableEntity) entity).getOwnerId() != null)
            return false;
        if (filterPlayers.isChecked() && entity instanceof PlayerEntity) return false;
        return true;
    }
}
