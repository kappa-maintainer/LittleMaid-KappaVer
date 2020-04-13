package net.firis.lmt.handler;


import net.blacklab.lmr.LittleMaidReengaged;
import net.firis.lmt.common.capability.IMaidAvatar;
import net.firis.lmt.common.capability.MaidAvatarProvider;
import net.firis.lmt.network.AvatarUpdatePacket;
import net.firis.lmt.network.PacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
/**
 * 
 * Capability handler
 * Responsible for attaching capability
 *
 */
public class MaidAvatarCapabilityHandler {
	public static final ResourceLocation MAID_AVATAR_CAPABILITY = new ResourceLocation(LittleMaidReengaged.DOMAIN,"model");
	
	@SubscribeEvent
	public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
		if(!(event.getObject() instanceof EntityPlayer))return;
		event.addCapability(MAID_AVATAR_CAPABILITY, new MaidAvatarProvider());
	}
	
	 @SubscribeEvent
	 public void onPlayerLogsIn(PlayerLoggedInEvent event)
	 {
		 EntityPlayer player = event.player;
		 PacketHandler.instance.sendToAll(new AvatarUpdatePacket(player));
	 }
	 
	 @SubscribeEvent
	 public void onPlayerDeath(PlayerEvent.Clone event) {
		 IMaidAvatar oldAvatar = event.getOriginal().getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
		 IMaidAvatar newAvatar = event.getEntityPlayer().getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
		 newAvatar.setIsAvatarEnable(oldAvatar.getIsAvatarEnable());
		 newAvatar.setAvatarModel(oldAvatar.getMainModel(), oldAvatar.getModelColor(), 
				 oldAvatar.getArmorModel(EntityEquipmentSlot.HEAD), 
				 oldAvatar.getArmorModel(EntityEquipmentSlot.CHEST), 
				 oldAvatar.getArmorModel(EntityEquipmentSlot.LEGS), 
				 oldAvatar.getArmorModel(EntityEquipmentSlot.FEET));
	 }
}
