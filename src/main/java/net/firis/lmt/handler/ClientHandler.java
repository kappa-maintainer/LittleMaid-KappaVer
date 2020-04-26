package net.firis.lmt.handler;

import net.blacklab.lmr.entity.maidmodel.ModelMultiBase;
import net.firis.lmt.common.LMTCore;
import net.firis.lmt.common.capability.IMaidAvatar;
import net.firis.lmt.common.capability.MaidAvatarProvider;
import net.firis.lmt.common.manager.PlayerModelManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientHandler {

	
	@SubscribeEvent
	public void onInputUpdate(InputUpdateEvent event) {
		if(!LMTCore.isLMTCore())return;
		EntityPlayer player = event.getEntityPlayer();
		IMaidAvatar avatar = player.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
		if(!avatar.getIsAvatarEnable())return;
		if(player.noClip)return;
		
		ModelMultiBase playerModel = PlayerModelManager.getPlayerModel(player);
		AxisAlignedBB notSneak = player.getEntityBoundingBox();
		
		float height = playerModel.getConditionalHeight(avatar.getIsSitting(), false, avatar.getIsWaiting(), null);
		
		if(height < 0.01F && height > -0.01F) return;
		
		notSneak = notSneak.setMaxY(notSneak.minY + height);
	    
	    if(!player.isSneaking() && !player.world.getCollisionBoxes(player, notSneak).isEmpty() && event.getMovementInput().sneak == false)
	    {
	      event.getMovementInput().sneak = true;
	      event.getMovementInput().moveStrafe = (float)((double)event.getMovementInput().moveStrafe * 0.3D);
	      event.getMovementInput().moveForward = (float)((double)event.getMovementInput().moveForward * 0.3D);
	    }


	}
}
