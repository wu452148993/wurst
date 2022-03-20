/*
 * Copyright (c) 2014-2022 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;

@SearchTags({"BoatFlight", "boat fly", "boat flight"})
public final class BoatFlyHack extends Hack implements UpdateListener {
    public BoatFlyHack() {
        super("BoatFly", "Allow your boat or horse fly.");
        setCategory(Category.MOVEMENT);
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
        // check if in boat
        if (!MC.player.isRowingBoat() || !MC.player.isRidingHorse())
            return;

        // fly
        Entity vehicle = MC.player.getRidingEntity();
        Vector3d velocity = vehicle.getPositionVec();
        double motionY = MC.gameSettings.keyBindJump.isKeyDown() ? 0.3 : 0;
        vehicle.setVelocity(velocity.x, motionY, velocity.z);
    }
}
