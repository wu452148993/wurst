/*
 * Copyright (c) 2014-2022 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import java.util.ArrayList;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.CameraTransformViewBobbingListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.util.RenderUtils;
import net.wurstclient.util.RotationUtils;

@SearchTags({"item esp", "ItemTracers", "item tracers"})
public final class ItemEspHack extends Hack implements UpdateListener,
        CameraTransformViewBobbingListener, RenderListener {
    private final EnumSetting<Style> style =
            new EnumSetting<>("Style", Style.values(), Style.BOXES);

    private final EnumSetting<BoxSize> boxSize = new EnumSetting<>("Box size",
            "\u00a7lAccurate\u00a7r mode shows the exact\n"
                    + "hitbox of each item.\n"
                    + "\u00a7lFancy\u00a7r mode shows larger boxes\n"
                    + "that look better.",
            BoxSize.values(), BoxSize.FANCY);

    private final ArrayList<ItemEntity> items = new ArrayList<>();

    public ItemEspHack() {
        super("ItemESP", "Highlights nearby items.");
        setCategory(Category.RENDER);

        addSetting(style);
        addSetting(boxSize);
    }

    @Override
    public void onEnable() {
        EVENTS.add(UpdateListener.class, this);
        EVENTS.add(CameraTransformViewBobbingListener.class, this);
        EVENTS.add(RenderListener.class, this);
    }

    @Override
    public void onDisable() {
        EVENTS.remove(UpdateListener.class, this);
        EVENTS.remove(CameraTransformViewBobbingListener.class, this);
        EVENTS.remove(RenderListener.class, this);
    }

    @Override
    public void onUpdate() {
        items.clear();
        for (Entity entity : MC.world.getAllEntities())
            if (entity instanceof ItemEntity)
                items.add((ItemEntity) entity);
    }

    @Override
    public void onCameraTransformViewBobbing(
            CameraTransformViewBobbingEvent event) {
        if (style.getSelected().lines)
            event.cancel();
    }

    @Override
    public void onRender(float partialTicks) {
        // GL settings
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);


        RenderUtils.applyRegionalRenderOffset();

        BlockPos camPos = RenderUtils.getCameraBlockPos();
        int regionX = (camPos.getX() >> 9) * 512;
        int regionZ = (camPos.getZ() >> 9) * 512;

        renderBoxes(partialTicks, regionX, regionZ);

        if (style.getSelected().lines)
            renderTracers(partialTicks, regionX, regionZ);

        // GL resets
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    private void renderBoxes(double partialTicks, int regionX, int regionZ)
    {
        double extraSize = boxSize.getSelected().extraSize;

        for(Entity e : items)
        {
            GL11.glPushMatrix();

            GL11.glTranslated(
                    e.lastTickPosX + (e.getPosX() - e.lastTickPosX) * partialTicks - regionX,
                    e.lastTickPosY + (e.getPosY() - e.lastTickPosY) * partialTicks,
                    e.lastTickPosZ + (e.getPosZ() - e.lastTickPosZ) * partialTicks - regionZ);

            GL11.glScaled(e.getWidth() + extraSize, e.getHeight() + extraSize,
                    e.getWidth() + extraSize);

            float f = MC.player.getDistance(e) / 20F;
            GL11.glColor4f(1, 1, 0, 0.5F);

            GL11.glPopMatrix();
        }
    }

    private void renderTracers(double partialTicks,
                               int regionX, int regionZ) {
        Vector3d start =
                RotationUtils.getClientLookVec().add(RenderUtils.getCameraPos());

        GL11.glBegin(GL11.GL_LINES);
        for (ItemEntity e : items) {
            Vector3d end = e.getBoundingBox().getCenter()
                    .subtract(new Vector3d(e.getPosX(), e.getPosY(), e.getPosZ())
                            .subtract(e.lastTickPosX, e.lastTickPosY, e.lastTickPosZ)
                            .scale(1 - partialTicks));

            GL11.glVertex3d(start.x - regionX, start.y, start.z - regionZ);
            GL11.glVertex3d(end.x - regionX, end.y, end.z - regionZ);
        }
        GL11.glEnd();
    }


    private enum Style {
        BOXES("Boxes only", true, false),
        LINES("Lines only", false, true),
        LINES_AND_BOXES("Lines and boxes", true, true);

        private final String name;
        private final boolean boxes;
        private final boolean lines;

        private Style(String name, boolean boxes, boolean lines) {
            this.name = name;
            this.boxes = boxes;
            this.lines = lines;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private enum BoxSize {
        ACCURATE("Accurate", 0),
        FANCY("Fancy", 0.1F);

        private final String name;
        private final float extraSize;

        private BoxSize(String name, float extraSize) {
            this.name = name;
            this.extraSize = extraSize;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
