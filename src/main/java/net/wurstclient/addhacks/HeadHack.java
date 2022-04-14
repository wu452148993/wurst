/*
 * Copyright (c) 2014-2022 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.addhacks;

import java.util.Comparator;
import java.util.Random;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.minecraft.client.entity.player.ClientPlayerEntity;
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
import net.minecraft.entity.passive.horse.TraderLlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.WurstClient;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.util.FakePlayerEntity;
import net.wurstclient.util.RotationUtils;

@SearchTags({"Head"})
public final class HeadHack extends Hack implements UpdateListener {
    private Random random = new Random();

    private final SliderSetting range =
            new SliderSetting("Range", 4.25, 1, 6, 0.05, ValueDisplay.DECIMAL);

    private final SliderSetting high =
            new SliderSetting("high", 1, 0, 6, 0.05, ValueDisplay.DECIMAL);

    private final EnumSetting<Priority> priority = new EnumSetting<>("Priority",
            "Determines which entity will be attacked first.\n"
                    + "\u00a7lDistance\u00a7r - Attacks the closest entity.\n"
                    + "\u00a7lAngle\u00a7r - Attacks the entity that requires\n"
                    + "the least head movement.\n"
                    + "\u00a7lHealth\u00a7r - Attacks the weakest entity.",
            Priority.values(), Priority.ANGLE);

    private final CheckboxSetting filterPlayers = new CheckboxSetting(
            "Filter players", "Won't attack other players.", false);
    private final CheckboxSetting filterSleeping = new CheckboxSetting(
            "Filter sleeping", "Won't attack sleeping players.", false);
    private final SliderSetting filterFlying =
            new SliderSetting("Filter flying",
                    "Won't attack players that\n" + "are at least the given\n"
                            + "distance above ground.",
                    0, 0, 2, 0.05,
                    v -> v == 0 ? "off" : ValueDisplay.DECIMAL.getValueString(v));

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

    private final CheckboxSetting filterNotAlive = new CheckboxSetting(
            "Filter Not Alive", "if not check will attack all entity.\n"
            + "(include entity like minecart)", true);

    public HeadHack() {
        super("Head", "Teleport you above the closest entity");
        setCategory(Category.COMBAT);

        addSetting(high);

        addSetting(range);
        addSetting(priority);

        addSetting(filterNotAlive);
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
    public void onEnable() {

        EVENTS.add(UpdateListener.class, this);
    }

    @Override
    public void onDisable() {
        EVENTS.remove(UpdateListener.class, this);
    }

    @Override
    public void onUpdate() {
        ClientPlayerEntity player = MC.player;

        // set entity
        double rangeSq = Math.pow(range.getValue(), 2);
        Stream<Entity> stream =
                StreamSupport.stream(MC.world.getAllEntities().spliterator(), true)
                        .filter(e -> e instanceof LivingEntity
                                && ((LivingEntity) e).getHealth() > 0
                                || e instanceof EnderCrystalEntity)
                        .filter(e -> player.getDistanceSq(e) <= rangeSq)
                        .filter(e -> e != player)
                        .filter(e -> !(e instanceof FakePlayerEntity));
//TODO                        .filter(e -> !WURST.getFriends().contains(e.getName()));

        if(filterNotAlive.isChecked())
            stream = stream.filter(e -> e instanceof LivingEntity
                    && ((LivingEntity)e).getHealth() > 0
                    || e instanceof EnderCrystalEntity);

        if (filterPlayers.isChecked())
            stream = stream.filter(e -> !(e instanceof PlayerEntity));

        if (filterSleeping.isChecked())
            stream = stream.filter(e -> !(e instanceof PlayerEntity
                    && ((PlayerEntity) e).isSleeping()));

        if (filterMonsters.isChecked())
            stream = stream.filter(e -> !(e instanceof MonsterEntity));

        if (filterPigmen.isChecked())
            stream = stream.filter(e -> !(e instanceof ZombifiedPiglinEntity));

        if (filterEndermen.isChecked())
            stream = stream.filter(e -> !(e instanceof EndermanEntity));

        if (filterAnimals.isChecked())
            stream = stream.filter(
                    e -> !(e instanceof AnimalEntity || e instanceof AmbientEntity ));
//TODO Fliter WaterCreatureEntity

/*TODO Fliter Baby
        if (filterBabies.isChecked())
            stream = stream.filter(e -> !(e instanceof PassiveEntity
                    && ((PassiveEntity) e).isBaby()));*/

        if (filterPets.isChecked())
            stream = stream
                    .filter(e -> !(e instanceof TameableEntity
                            && ((TameableEntity) e).isTamed()))
                    .filter(e -> !(e instanceof HorseEntity
                            && ((HorseEntity) e).isTame()));

        if (filterTraders.isChecked())
            stream = stream.filter(e -> !(e instanceof VillagerEntity));

        if (filterGolems.isChecked())
            stream = stream.filter(e -> !(e instanceof GolemEntity));

        if (filterInvisible.isChecked())
            stream = stream.filter(e -> !e.isInvisible());

        if (filterNamed.isChecked())
            stream = stream.filter(e -> !e.hasCustomName());

        if (filterStands.isChecked())
            stream = stream.filter(e -> !(e instanceof ArmorStandEntity));

        if (filterCrystals.isChecked())
            stream = stream.filter(e -> !(e instanceof EnderCrystalEntity));

        Entity entity =
                stream.min(priority.getSelected().comparator).orElse(null);

        if (entity == null)
            return;

        // teleport
        player.setPosition(entity.getPosX(),
                entity.getPosY() + high.getValue(), entity.getPosZ());
    }

    private enum Priority {
        DISTANCE("Distance", e -> MC.player.getDistanceSq(e)),

        ANGLE("Angle",
                e -> RotationUtils
                        .getAngleToLookVec(e.getBoundingBox().getCenter())),

        HEALTH("Health", e -> e instanceof LivingEntity
                ? ((LivingEntity) e).getHealth() : Integer.MAX_VALUE);

        private final String name;
        private final Comparator<Entity> comparator;

        private Priority(String name, ToDoubleFunction<Entity> keyExtractor) {
            this.name = name;
            comparator = Comparator.comparingDouble(keyExtractor);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
