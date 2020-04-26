package net.firis.lmt.client.event;

import net.blacklab.lmr.entity.maidmodel.ModelMultiBase;
import net.firis.lmt.common.capability.IMaidAvatar;
import net.firis.lmt.common.capability.MaidAvatarProvider;
import net.firis.lmt.common.manager.PlayerModelManager;
import net.firis.lmt.common.modelcaps.PlayerModelCaps;
import net.firis.lmt.network.MaidSittingUpdatePacket;
import net.firis.lmt.network.MaidWaitingUpdatePacket;
import net.firis.lmt.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LittleMaidAvatarClientTickEventHandler {
	
	@SubscribeEvent
	public static void onClientTickEvent(ClientTickEvent event) {
		if(event.phase == TickEvent.Phase.END
				&& Minecraft.getMinecraft().player != null){
			onClientTickEventLittleMaidAvatar(event);
		}
	}
	
	/**
	 * アバターアクションの管理
	 * @param event
	 */
	protected static void onClientTickEventLittleMaidAvatar(ClientTickEvent event) {
		
		boolean isMotionSittingReset = false;
		boolean isMotionWaitReset = false;
		
		EntityPlayer player = Minecraft.getMinecraft().player;
		IMaidAvatar avatar = player.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
		
		//アクション解除
		//腕振り
		if (!isMotionWaitReset && (player.swingProgress > 0.01F || player.swingProgress < -0.01F)) {
			isMotionWaitReset = true;
		}
		
		//右クリック
		if (!isMotionWaitReset) {
	        if (EnumAction.NONE != PlayerModelCaps.getPlayerAction(player, EnumHandSide.RIGHT)
	        		|| EnumAction.NONE != PlayerModelCaps.getPlayerAction(player, EnumHandSide.LEFT)) {
	        	isMotionWaitReset = true;	        	
	        }
		}
		
		//アクション解除
		//縦方向は重力が発生してるので微調整して判断
		if (!isMotionWaitReset || !isMotionSittingReset) {
			if (player.motionX > 0.01D || player.motionX < -0.01D
					|| player.motionZ > 0.01D || player.motionZ < -0.01D
					|| player.motionY > 0.01D) {
				ModelMultiBase playerModel = PlayerModelManager.getPlayerModel(player);
				
				float height = playerModel.getConditionalHeight(false, false, false, null);
				if(height < 0.01F && height > -0.01F) {
					isMotionWaitReset = true;
					isMotionSittingReset = true;
				}
				AxisAlignedBB notSit = player.getEntityBoundingBox();
				notSit = notSit.setMaxY(notSit.minY + height);
				if(player.world.getCollisionBoxes(player, notSit).isEmpty()) {
					isMotionWaitReset = true;
					isMotionSittingReset = true;
				} else {
					isMotionSittingReset =false;
					isMotionWaitReset = false;
				}
				
			}
		}
		
		if (isMotionWaitReset) {
			//待機モーションリセット
			avatar.setIsWaiting(false);
			PacketHandler.instance.sendToServer(new MaidWaitingUpdatePacket(player.getEntityId(), false));
			avatar.setWaitCounter(player.ticksExisted);
		}
		if (isMotionSittingReset) {
			//お座りモーションリセット
			avatar.setIsSitting(false);
			PacketHandler.instance.sendToServer(new MaidSittingUpdatePacket(player.getEntityId(), false));
		}
		
		if (!isMotionWaitReset && !isMotionSittingReset) {
			
			//モーション継続状態と判断
			Integer counter = avatar.getWaitCounter();
			
			//初期化
			if (counter == 0) avatar.setWaitCounter(player.ticksExisted);
			
			//100tickで待機状態On
			if ((player.ticksExisted - counter) >= 100) {
				avatar.setIsWaiting(true);
				PacketHandler.instance.sendToServer(new MaidWaitingUpdatePacket(player.getEntityId(), true));
			}
		}
	}
}
