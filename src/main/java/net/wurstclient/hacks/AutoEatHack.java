/*
 * Copyright (c) 2014-2022 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Foods;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.datafix.fixes.BlockEntityUUID;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.EnumSetting;

import java.util.Comparator;

@SearchTags({"auto eat", "AutoFood", "auto food", "AutoFeeder", "auto feeder",
        "AutoFeeding", "auto feeding", "AutoSoup", "auto soup"})
public final class AutoEatHack extends Hack implements UpdateListener {
    private final CheckboxSetting eatWhileWalking = new CheckboxSetting(
            "Eat while walking", "Slows you down, not recommended.", false);

    private final EnumSetting<FoodPriority> foodPriority =
            new EnumSetting<>("Prefer food with", FoodPriority.values(),
                    FoodPriority.HIGH_SATURATION);

    private final CheckboxSetting allowHunger =
            new CheckboxSetting("Allow hunger effect",
                    "Rotten flesh applies a harmless 'hunger' effect.\n"
                            + "It is safe to eat and useful as emergency food.",
                    true);

    private final CheckboxSetting allowPoison =
            new CheckboxSetting("Allow poison effect",
                    "Poisoned food applies damage over time.\n" + "Not recommended.",
                    false);

    private final CheckboxSetting allowChorus =
            new CheckboxSetting("Allow chorus fruit",
                    "Eating chorus fruit teleports you to a random location.\n"
                            + "Not recommended.",
                    false);

    private int oldSlot = -1;

    public AutoEatHack() {
        super("AutoEat", "");
        setCategory(Category.ITEMS);
        addSetting(eatWhileWalking);
        addSetting(foodPriority);
        addSetting(allowHunger);
        addSetting(allowPoison);
        addSetting(allowChorus);
    }

    @Override
    public void onEnable() {
        EVENTS.add(UpdateListener.class, this);
    }

    @Override
    public void onDisable() {
        EVENTS.remove(UpdateListener.class, this);
        stopIfEating();
    }

    @Override
    public void onUpdate() {
        if (!shouldEat()) {
            stopIfEating();
            return;
        }

        int bestSlot = getBestSlot();
        if (bestSlot == -1) {
            stopIfEating();
            return;
        }

        // save old slot
        if (!isEating())
            oldSlot = MC.player.inventory.currentItem;

        // set slot
        MC.player.inventory.currentItem = bestSlot;

        // eat food
        MC.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
    }

    private int getBestSlot() {
        int bestSlot = -1;
        Food bestFood = null;
        Comparator<Food> comparator =
                foodPriority.getSelected().comparator;

        for (int i = 0; i < 9; i++) {
            // filter out non-food items
            Item item = MC.player.inventory.getCurrentItem().getItem();
            if (!item.isFood())
                continue;

            Food food = item.getFood();
            if (!isAllowedFood(food))
                continue;

            // compare to previously found food
            if (bestFood == null || comparator.compare(food, bestFood) > 0) {
                bestFood = food;
                bestSlot = i;
            }
        }

        return bestSlot;
    }

    private boolean isAllowedFood(Food food) {
        if (!allowChorus.isChecked() && food == Foods.CHORUS_FRUIT)
            return false;

        for (Pair<EffectInstance, Float> pair : food.getEffects()) {
            Effect effect = pair.getFirst().getPotion();

            if (!allowHunger.isChecked() && effect == Effects.HUNGER)
                return false;

            if (!allowPoison.isChecked() && effect == Effects.POISON)
                return false;
        }

        return true;
    }

    private boolean shouldEat() {
        if (MC.player.abilities.isCreativeMode)
            return false;

        if (!MC.player.canEat(false))
            return false;

        if (!eatWhileWalking.isChecked()
                && (MC.player.moveVertical != 0 || MC.player.moveForward != 0))
            return false;

        if (isClickable(MC.objectMouseOver))
            return false;

        return true;
    }

    private boolean isClickable(RayTraceResult hitResult) {
        if (hitResult == null)
            return false;

        if (hitResult.getType() == RayTraceResult.Type.ENTITY) {
            Entity entity = ((EntityRayTraceResult) hitResult).getEntity();
            return entity instanceof VillagerEntity
                    || entity instanceof TameableEntity;
        }

        if (hitResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockPos pos = ((BlockRayTraceResult) hitResult).getPos();
            if (pos == null)
                return false;

            Block block = MC.world.getBlockState(pos).getBlock();
            return block instanceof ITileEntityProvider
                    || block instanceof CraftingTableBlock;
        }

        return false;
    }

    public boolean isEating() {
        return oldSlot != -1;
    }

    private void stopIfEating() {
        if (!isEating())
            return;

        MC.gameSettings.keyBindUseItem.setPressed(false);

        MC.player.inventory.currentItem = oldSlot;
        oldSlot = -1;
    }

    public static enum FoodPriority {
        HIGH_HUNGER("High Food Points",
                Comparator.<Food>comparingInt(Food::getHealing)),

        HIGH_SATURATION("High Saturation",
                Comparator.<Food>comparingDouble(
                        Food::getSaturation)),

        LOW_HUNGER("Low Food Points",
                Comparator.<Food>comparingInt(Food::getHealing)
                        .reversed()),

        LOW_SATURATION("Low Saturation",
                Comparator.<Food>comparingDouble(
                        Food::getSaturation).reversed());

        private final String name;
        private final Comparator<Food> comparator;

        private FoodPriority(String name, Comparator<Food> comparator) {
            this.name = name;
            this.comparator = comparator;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
