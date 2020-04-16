package net.firis.lmt.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

public class DefaultBoxSwitcher {
	public static void setDefaultBox(EntityPlayer playerIn) {
		playerIn.eyeHeight = playerIn.getDefaultEyeHeight();
        float f;
        float f1;

        if (playerIn.isElytraFlying())
        {
            f = 0.6F;
            f1 = 0.6F;
        }
        else if (playerIn.isPlayerSleeping())
        {
            f = 0.2F;
            f1 = 0.2F;
        }
        else if (playerIn.isSneaking())
        {
            f = 0.6F;
            f1 = 1.65F;
        }
        else
        {
            f = 0.6F;
            f1 = 1.8F;
        }
        AxisAlignedBB box = playerIn.getEntityBoundingBox();
		playerIn.setEntityBoundingBox(new AxisAlignedBB(playerIn.posX - f/2, box.minY, playerIn.posZ - f/2, playerIn.posX + f/2, box.minY + f1, playerIn.posZ + f/2));
	}
}
