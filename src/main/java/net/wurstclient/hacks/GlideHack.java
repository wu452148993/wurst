/*
 * Copyright (c) 2014-2022 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.mojang.datafixers.optics.Lens;
import net.minecraft.block.BlockState;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.wurstclient.Category;
import net.wurstclient.WurstClient;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.util.BlockUtils;

public final class GlideHack extends Hack implements UpdateListener {
    private final SliderSetting fallSpeed = new SliderSetting("Fall speed",
            0.125, 0.005, 0.25, 0.005, ValueDisplay.DECIMAL);

    private final SliderSetting moveSpeed =
            new SliderSetting("Move speed", "Horizontal movement factor.", 1.2, 1,
                    5, 0.05, ValueDisplay.PERCENTAGE);

    public GlideHack() {
        super("Glide", "");

        setCategory(Category.MOVEMENT);
        addSetting(fallSpeed);
        addSetting(moveSpeed);
    }

    @Override
    public void onEnable() {
        WURST.getHax().flightHack.setEnabled(false);

        EVENTS.add(UpdateListener.class, this);
    }

    @Override
    public void onDisable() {
        EVENTS.remove(UpdateListener.class, this);
    }

    @Override
    public void onUpdate() {
        ClientPlayerEntity player = MC.player;
        Vector3d v = player.getMotion();

        if (player.isOnGround() || player.isInWater() || player.isInLava()
                || player.isOnLadder() || v.y >= 0)
            return;

        player.setVelocity(v.x, Math.max(v.y, -fallSpeed.getValue()), v.z);
        player.jumpMovementFactor *= moveSpeed.getValueF();

    }
}
