package net.firis.lmt.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.blacklab.lmr.entity.maidmodel.ModelLittleMaidBase;
import net.blacklab.lmr.entity.maidmodel.ModelMultiBase;
import net.firis.lmt.common.manager.PlayerModelManager;
import net.firis.lmt.config.FirisConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CommonHandler {
	public static Method setSize = ObfuscationReflectionHelper.findMethod(Entity.class, "func_70105_a", void.class, float.class, float.class);
	
	@SubscribeEvent
	public void adjustSize(TickEvent.PlayerTickEvent event) {
		if(FirisConfig.cfg_immersive_avatar) {
			EntityPlayer player = event.player;
			if(player.isPlayerSleeping() || player.isElytraFlying()) return;
			ModelMultiBase playerModel = PlayerModelManager.getPlayerModel(player);
			AxisAlignedBB box = player.getEntityBoundingBox();
			double height = ((ModelLittleMaidBase)(playerModel)).getHeight();
			double width = ((ModelLittleMaidBase)(playerModel)).getWidth();
			double d0 = width / 2.0D;
			double eyeheight;
			if(player.isSneaking()) {
				height *= 1.5D / 1.8D;
			}
			eyeheight = height - 0.25D;
			try {
				setSize.invoke(player, (float)width, (float)height);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			player.eyeHeight = (float) eyeheight;
			player.setEntityBoundingBox(new AxisAlignedBB(player.posX - d0, box.minY, player.posZ - d0, player.posX + d0, box.minY + height, player.posZ + d0));
			
		}
	}

}
