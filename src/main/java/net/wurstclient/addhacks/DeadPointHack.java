/*
 * Copyright (c) 2014-2022 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.addhacks;

import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.util.ChatUtils;

@SearchTags({"Death"})
public final class DeadPointHack extends Hack implements UpdateListener {

    //this is a bad thing to make a non-mean var, but I don't know how to do it better.
    //if (player is alive) {
    // MC.player.deathTime = 0
    // }
    // and player after death deathTime will increase.
    private int dead = 0;

    public DeadPointHack() {
        super("DeathPoint", "Show your Death Point");
        setCategory(Category.CHAT);
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
        if(MC.player.deathTime != 0 && dead == 0) {
            ChatUtils.message("You dead at "
                    + (int)MC.player.getPosX() + " , "
                    + (int)MC.player.getPosY() + " , "
                    + (int)MC.player.getPosZ() + " . ");
            dead = 1;
        } else {
            dead = 0;
        }
    }
}
