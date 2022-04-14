/*
 * Copyright (c) 2014-2022 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.entity.LivingEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CUpdateJigsawBlockPacket;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.LeftClickListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.EnumSetting;

@SearchTags({"Crits"})
public final class CriticalsHack extends Hack implements LeftClickListener
{
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode",
            "\u00a7lPacket\u00a7r mode sends packets to server without actually\n"
                    + "moving you at all.\n\n"
                    + "\u00a7lMini Jump\u00a7r mode does a tiny jump that is just enough to\n"
                    + "get a critical hit.\n\n"
                    + "\u00a7lFull Jump\u00a7r mode makes you jump normally.",
            Mode.values(), Mode.PACKET);

    public CriticalsHack()
    {
        super("Criticals","");
        setCategory(Category.COMBAT);
        addSetting(mode);
    }

    @Override
    public String getRenderName()
    {
        return getName() + " [" + mode.getSelected() + "]";
    }

    @Override
    public void onEnable()
    {
        EVENTS.add(LeftClickListener.class, this);
    }

    @Override
    public void onDisable()
    {
        EVENTS.remove(LeftClickListener.class, this);
    }

    @Override
    public void onLeftClick(LeftClickEvent event)
    {

        if(MC.objectMouseOver == null
                || !(MC.objectMouseOver instanceof EntityRayTraceResult)
                ||!(((EntityRayTraceResult) MC.objectMouseOver).getEntity() instanceof LivingEntity))
            return;

        doCritical();
    }

    public void doCritical()
    {
        if(!isEnabled())
            return;

        if(!MC.player.isOnGround())
            return;

        if(MC.player.isInWater() || MC.player.isInLava())
            return;

        switch(mode.getSelected())
        {
            case PACKET:
                doPacketJump();
                break;

            case MINI_JUMP:
                doMiniJump();
                break;

            case FULL_JUMP:
                doFullJump();
                break;
        }
    }

    private void doPacketJump()
    {
        double posX = MC.player.getPosX();
        double posY = MC.player.getPosY();
        double posZ = MC.player.getPosZ();

        sendPos(posX, posY + 0.0625D, posZ, true);
        sendPos(posX, posY, posZ, false);
        sendPos(posX, posY + 1.1E-5D, posZ, false);
        sendPos(posX, posY, posZ, false);

    }

    private void sendPos(double x, double y, double z, boolean onGround)
    {
        MC.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, onGround));

    }

    private void doMiniJump()
    {
        MC.player.addVelocity(0, 0.1, 0);
        MC.player.fallDistance = 0.1F;
        MC.player.setOnGround(false);

    }

    private void doFullJump()
    {
        MC.player.jump();

    }

    private enum Mode
    {
        PACKET("Packet"),
        MINI_JUMP("Mini Jump"),
        FULL_JUMP("Full Jump");

        private final String name;

        private Mode(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }
}
