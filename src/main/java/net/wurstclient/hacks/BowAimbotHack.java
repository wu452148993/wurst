/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import java.util.Comparator;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.GUIRenderListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.util.FakePlayerEntity;
import net.wurstclient.util.RenderUtils;
import net.wurstclient.util.RotationUtils;

@SearchTags({"bow aimbot"})
public final class BowAimbotHack extends Hack
	implements UpdateListener, RenderListener, GUIRenderListener
{
	private final EnumSetting<Priority> priority = new EnumSetting<>("Priority",
		"Determines which entity will be attacked first.\n"
			+ "\u00a7lDistance\u00a7r - Attacks the closest entity.\n"
			+ "\u00a7lAngle\u00a7r - Attacks the entity that requires\n"
			+ "the least head movement.\n"
			+ "\u00a7lHealth\u00a7r - Attacks the weakest entity.",
		Priority.values(), Priority.ANGLE);
	
	private final SliderSetting predictMovement =
		new SliderSetting("Predict movement",
			"Controls the strength of BowAimbot's\n"
				+ "movement prediction algorithm.",
			0.2, 0, 2, 0.01, ValueDisplay.PERCENTAGE);

	private final CheckboxSetting targetVisible = new CheckboxSetting(
			"Visible Targets", "Only snap to visible targets.", false);
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

	private static final AxisAlignedBB TARGET_BOX =
			new AxisAlignedBB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5);

	private Entity target;
	private float velocity;
	private static boolean trigger;
	
	public BowAimbotHack()
	{
		super("BowAimbot", "Automatically aims your bow or crossbow.");
		
		setCategory(Category.COMBAT);
		addSetting(priority);
		addSetting(predictMovement);
		addSetting(targetVisible);
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

	public boolean canEntityBeSeen(Entity entityIn) {
		Vector3d vector3d = new Vector3d(MC.player.getPosX(), MC.player.getPosYEye(), MC.player.getPosZ());
		Vector3d vector3d1 = new Vector3d(entityIn.getPosX(), entityIn.getPosYEye(), entityIn.getPosZ());
		if(vector3d != null && vector3d1 != null && targetVisible.isChecked()) {
			return MC.world.rayTraceBlocks(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, MC.player)).getType() == RayTraceResult.Type.MISS;
		} else if (!targetVisible.isChecked()) {
			return true;
		}else {
			return false;
		}
	}

	@Override
	public void onEnable()
	{
		EVENTS.add(GUIRenderListener.class, this);
		EVENTS.add(RenderListener.class, this);
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable() {
		EVENTS.remove(GUIRenderListener.class, this);
		EVENTS.remove(RenderListener.class, this);
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		ClientPlayerEntity player = MC.player;
		
		// check if item is ranged weapon
		ItemStack stack = MC.player.inventory.getCurrentItem();
		Item item = stack.getItem();
		if(!(item instanceof BowItem || item instanceof CrossbowItem))
		{
			target = null;
			return;
		}

		// check if using bow
		if(item instanceof BowItem && !MC.gameSettings.keyBindUseItem.isKeyDown()
				&& !player.isHandActive())
		{
			target = null;
			return;
		}
		
		// check if crossbow is loaded
		if(item instanceof CrossbowItem && !CrossbowItem.isCharged(stack))
		{
			target = null;
			return;
		}
		
		// set target
		if(filterEntities(Stream.of(target)) == null)
			target = filterEntities(StreamSupport
				.stream(MC.world.getAllEntities().spliterator(), true));
		
		if(target == null || !canEntityBeSeen(target))
			return;

		// set velocity
		velocity = (72000 - player.getItemInUseCount()) / 20F;
		velocity = (velocity * velocity + velocity * 2) / 3;
		if(velocity > 1)
			velocity = 1;
		/*
		float f = (float)20 / 20.0F;
		velocity = (f * f + f * 2.0F) / 3.0F;
		if (velocity > 1.0F) {
			velocity = 1.0F;
		}*/
		
		// set position to aim at
		double d = RotationUtils.getEyesPos().distanceTo(
			target.getBoundingBox().getCenter()) * predictMovement.getValue();
		double posX = target.getPosX() + (target.getPosX() - target.lastTickPosX) * d
			- player.getPosX() ;
		double posY = target.getPosY()  + (target.getPosY() - target.lastTickPosY) * d
			+ target.getHeight() * 0.5 - player.getPosY()
			- player.getEyeHeight(player.getPose())+0.4;
		double posZ = target.getPosZ() + (target.getPosZ() - target.lastTickPosZ) * d
			- player.getPosZ();
		
		// set yaw
		MC.player.rotationYaw = (float)Math.toDegrees(Math.atan2(posZ, posX)) - 90;
		
		// calculate needed pitch
		double hDistance = Math.sqrt(posX * posX + posZ * posZ);
		double hDistanceSq = hDistance * hDistance;
		float g = 0.006F;
		float velocitySq = velocity * velocity;
		float velocityPow4 = velocitySq * velocitySq;
		float neededPitch = (float)-Math.toDegrees(Math.atan((velocitySq - Math
			.sqrt(velocityPow4 - g * (g * hDistanceSq + 2 * posY * velocitySq)))
			/ (g * hDistance)));
		
		// set pitch
		if(Float.isNaN(neededPitch))
			WURST.getRotationFaker()
				.faceVectorClient(target.getBoundingBox().getCenter());
		else
			MC.player.rotationPitch = neededPitch;
	}

	private Entity filterEntities(Stream<Entity> s)
	{
		Stream<Entity> stream = s.filter(e -> e != null && !e.removed).filter(
			e -> e instanceof LivingEntity && ((LivingEntity)e).getHealth() > 0
					|| e instanceof EnderCrystalEntity)
			.filter(e -> e != MC.player)
			.filter(e -> !(e instanceof FakePlayerEntity))
			.filter(e -> !WURST.getFriends().contains(e.getScoreboardName()));
		
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
				return !MC.world.hasNoCollisions(box);
			});

		if(filterMonsters.isChecked())
			stream = stream.filter(e -> !(e instanceof MonsterEntity));
		
		if(filterPigmen.isChecked())
			stream = stream.filter(e -> !(e instanceof ZombifiedPiglinEntity));
		
		if(filterEndermen.isChecked())
			stream = stream.filter(e -> !(e instanceof EndermanEntity));
		
		if(filterAnimals.isChecked()) {
			stream = stream.filter(
				e -> !(e instanceof AnimalEntity || e instanceof AmbientEntity
					|| e instanceof WaterMobEntity));
		}

		if(filterBabies.isChecked())
			stream = stream.filter(e -> !(e instanceof AgeableEntity
					&& ((AgeableEntity)e).isChild()));

		if(filterPets.isChecked())
			stream = stream
				.filter(e -> !(e instanceof TameableEntity
					&& ((TameableEntity)e).isTamed()))
				.filter(e -> !(e instanceof HorseEntity
					&& ((HorseEntity)e).isTame()));

		if(filterTraders.isChecked())
			stream = stream.filter(e -> !(e instanceof AbstractVillagerEntity));

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

		return stream.min(priority.getSelected().comparator).orElse(null);
	}

	@Override
	public void onRender(float partialTicks)
	{
		if(target == null)
			return;
		
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		GL11.glPushMatrix();
		RenderUtils.applyRenderOffset();
		
		// set position
		GL11.glTranslated(target.getPosX(), target.getPosY(), target.getPosZ());
		
		// set size
		double boxWidth = target.getWidth() + 0.1;
		double boxHeight = target.getHeight() + 0.1;
		GL11.glScaled(boxWidth, boxHeight, boxWidth);
		
		// move to center
		GL11.glTranslated(0, 0.5, 0);
		
		double v = 1 / velocity;
		GL11.glScaled(v, v, v);
		
		// draw outline
		GL11.glColor4d(1, 0, 0, 0.5F * velocity);
		RenderUtils.drawOutlinedBox(TARGET_BOX);
		
		// draw box
		GL11.glColor4d(1, 0, 0, 0.25F * velocity);
		RenderUtils.drawSolidBox(TARGET_BOX);
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	
	@Override
	public void onRenderGUI(MatrixStack matrixStack, float partialTicks)
	{
		if(target == null)
			return;
		
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		GL11.glPushMatrix();
		
		String message;
		if(velocity < 1)
			message = "Charging: " + (int)(velocity * 100) + "%";
		else
			message = "Target Locked";

		// translate to center
		MainWindow sr = MC.getMainWindow();
		int msgWidth = MC.fontRenderer.getStringWidth(message);
		GL11.glTranslated(sr.getScaledWidth() / 2 - msgWidth / 2,
				sr.getScaledHeight() / 2 + 1, 0);

		// background
		GL11.glColor4f(0, 0, 0, 0.5F);
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2d(0, 0);
			GL11.glVertex2d(msgWidth + 3, 0);
			GL11.glVertex2d(msgWidth + 3, 10);
			GL11.glVertex2d(0, 10);
		}
		GL11.glEnd();

		// text
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		MC.fontRenderer.drawString(matrixStack, message, 2, 1, 0xffffffff);
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	private enum Priority
	{
		DISTANCE("Distance", e -> MC.player.getDistanceSq(e)),
		
		ANGLE("Angle",
			e -> RotationUtils
				.getAngleToLookVec(e.getBoundingBox().getCenter())),
		
		HEALTH("Health", e -> e instanceof LivingEntity
			? ((LivingEntity)e).getHealth() : Integer.MAX_VALUE);
		
		private final String name;
		private final Comparator<Entity> comparator;
		
		private Priority(String name, ToDoubleFunction<Entity> keyExtractor)
		{
			this.name = name;
			comparator = Comparator.comparingDouble(keyExtractor);
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
