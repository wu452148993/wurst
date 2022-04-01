/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AmbientEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.util.FakePlayerEntity;

import java.util.Objects;
import java.util.stream.Stream;

@SearchTags({"trigger bot"})
public final class TriggerBotHack extends Hack implements UpdateListener
{
    private final SliderSetting range =
            new SliderSetting("Range", 4.25, 1, 6, 0.05, SliderSetting.ValueDisplay.DECIMAL);

    private final CheckboxSetting filterPlayers = new CheckboxSetting(
            "Filter players", "Won't attack other players.", false);
    private final CheckboxSetting filterSleeping = new CheckboxSetting(
            "Filter sleeping", "Won't attack sleeping players.", false);
    private final SliderSetting filterFlying =
            new SliderSetting("Filter flying",
                    "Won't attack players that\n" + "are at least the given\n"
                            + "distance above ground.",
                    0, 0, 2, 0.05,
                    v -> v == 0 ? "off" : SliderSetting.ValueDisplay.DECIMAL.getValueString(v));

    private final CheckboxSetting filterMonsters = new CheckboxSetting(
            "Filter monsters", "Won't attack zombies, creepers, etc.", false);
    private final CheckboxSetting filterPigmen = new CheckboxSetting(
            "Filter pigmen", "Won't attack zombie pigmen.", false);
    private final CheckboxSetting filterEndermen =
            new CheckboxSetting("Filter endermen", "Won't attack endermen.", false);

    private final CheckboxSetting filterAnimals = new CheckboxSetting(
            "Filter animals", "Won't attack pigs, cows, etc.", false);
    private final CheckboxSetting filterBabies =
            new CheckboxSetting("Filter babies",
                    "Won't attack baby pigs,\n" + "baby villagers, etc.", false);
    private final CheckboxSetting filterPets =
            new CheckboxSetting("Filter pets",
                    "Won't attack tamed wolves,\n" + "tamed horses, etc.", false);

    private final CheckboxSetting filterTraders =
            new CheckboxSetting("Filter traders",
                    "Won't attack villagers, wandering traders, etc.", false);

    private final CheckboxSetting filterGolems =
            new CheckboxSetting("Filter golems",
                    "Won't attack iron golems,\n" + "snow golems and shulkers.", false);

    private final CheckboxSetting filterInvisible = new CheckboxSetting(
            "Filter invisible", "Won't attack invisible entities.", false);
    private final CheckboxSetting filterNamed = new CheckboxSetting(
            "Filter named", "Won't attack name-tagged entities.", false);

    private final CheckboxSetting filterStands = new CheckboxSetting(
            "Filter armor stands", "Won't attack armor stands.", false);
    private final CheckboxSetting filterCrystals = new CheckboxSetting(
            "Filter end crystals", "Won't attack end crystals.", false);

    public TriggerBotHack()
    {
        super("TriggerBot","Automatically attacks the entity you're looking at.");
        setCategory(Category.COMBAT);
        addSetting(range);
        addSetting(filterPlayers);
        addSetting(filterSleeping);
        addSetting(filterFlying);
        addSetting(filterMonsters);
        addSetting(filterPigmen);
        addSetting(filterEndermen);
        addSetting(filterAnimals);
        addSetting(filterBabies);
        addSetting(filterPets);
        addSetting(filterTraders);
        addSetting(filterGolems);
        addSetting(filterInvisible);
        addSetting(filterNamed);
        addSetting(filterStands);
        addSetting(filterCrystals);
    }

    @Override
    public void onEnable()
    {
        // disable other killauras
        WURST.getHax().multiAuraHack.setEnabled(false);
        WURST.getHax().tpAuraHack.setEnabled(false);

        EVENTS.add(UpdateListener.class, this);
    }

    @Override
    public void onDisable()
    {
        EVENTS.remove(UpdateListener.class, this);
    }

    @Override
    public void onUpdate()
    {
        ClientPlayerEntity player = MC.player;
        if(player.getCooledAttackStrength(0) < 1)
            return;

        if(!(MC.objectMouseOver instanceof EntityRayTraceResult))
            return;

        Entity target = ((EntityRayTraceResult)MC.objectMouseOver).getEntity();
        if(!isCorrectEntity(target))
            return;

        WURST.getHax().criticalsHack.doCritical();
        MC.playerController.attackEntity(player, target);
        player.swing(Hand.MAIN_HAND,true);
    }

    private boolean isCorrectEntity(Entity entity)
    {
        ClientPlayerEntity player = MC.player;
        ClientWorld world = MC.world;

        double rangeSq = Math.pow(range.getValue(), 2);
        Stream<Entity> stream = Stream.of(entity).filter(e -> !e.removed)
                .filter(e -> e instanceof LivingEntity
                        && ((LivingEntity)e).getHealth() > 0
                        || e instanceof EnderCrystalEntity)
                .filter(e -> player.getDistanceSq(e) <= rangeSq)
                .filter(e -> e != player)
                .filter(e -> !(e instanceof FakePlayerEntity))
                .filter(e -> !(Objects.equals(e.getEntityString(), "aoa3:bloodlust"))) //Advent of Ascension 3 BloodLust
                .filter(e -> !WURST.getFriends().contains(e.getName().getString()));

        if(filterPlayers.isChecked())
            stream = stream.filter(e -> !(e instanceof PlayerEntity));

        if(filterSleeping.isChecked())
            stream = stream.filter(e -> !(e instanceof PlayerEntity
                    && ((PlayerEntity)e).isSleeping()));

        if(filterFlying.getValue() > 0)
            stream = stream.filter(e -> {

                if(!(e instanceof PlayerEntity))
                    return true;

                AxisAlignedBB box = e.getBoundingBox();
                box = box.union(box.offset(0, -filterFlying.getValue(), 0));
                return !world.hasNoCollisions(box);
            });

        if(filterMonsters.isChecked())
            stream = stream.filter(e -> !(e instanceof MonsterEntity));

        if(filterPigmen.isChecked())
            stream = stream.filter(e -> !(e instanceof ZombifiedPiglinEntity));

        if(filterEndermen.isChecked())
            stream = stream.filter(e -> !(e instanceof EndermanEntity));

        if(filterAnimals.isChecked())
            stream = stream.filter(
                    e -> !(e instanceof AnimalEntity || e instanceof AmbientEntity));
/*TODO Filter WaterCreatureEntity                            || e instanceof WaterCreatureEntity*/

/*TODO Filter Baby
          if(filterBabies.isChecked())
            stream = stream.filter(e -> !(e instanceof PassiveEntity
                    && ((PassiveEntity)e).isBaby()));*/

        if(filterPets.isChecked())
            stream = stream
                    .filter(e -> !(e instanceof TameableEntity
                            && ((TameableEntity)e).isTamed()))
                    .filter(e -> !(e instanceof HorseEntity
                            && ((HorseEntity)e).isTame()));

        if(filterTraders.isChecked())
            stream = stream.filter(e -> !(e instanceof VillagerEntity));

        if(filterGolems.isChecked())
            stream = stream.filter(e -> !(e instanceof GolemEntity));

        if(filterInvisible.isChecked())
            stream = stream.filter(e -> !e.isInvisible());

        if(filterNamed.isChecked())
            stream = stream.filter(e -> !e.hasCustomName());

        if(filterStands.isChecked())
            stream = stream.filter(e -> !(e instanceof ArmorStandEntity));

        if(filterCrystals.isChecked())
            stream = stream.filter(e -> !(e instanceof EnderCrystalEntity));

        return stream.findFirst().isPresent();
    }
}

