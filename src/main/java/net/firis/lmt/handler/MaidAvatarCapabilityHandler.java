package net.firis.lmt.handler;


import net.blacklab.lmr.LittleMaidReengaged;
import net.blacklab.lmr.entity.littlemaid.EntityLittleMaidAvatarMP;
import net.blacklab.lmr.entity.littlemaid.IEntityLittleMaidAvatar;
import net.firis.lmt.common.capability.IMaidAvatar;
import net.firis.lmt.common.capability.MaidAvatarProvider;
import net.firis.lmt.network.AvatarUpdatePacket;
import net.firis.lmt.network.PacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
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
		if(!(event.getObject() instanceof EntityPlayer) || (event.getObject() instanceof IEntityLittleMaidAvatar))return;
		event.addCapability(MAID_AVATAR_CAPABILITY, new MaidAvatarProvider());
		EntityPlayer player = (EntityPlayer)event.getObject();
		LittleMaidReengaged.logger.info("ATTACH COUNT " + FMLCommonHandler.instance().getSide().name() + " " + player.getUniqueID().toString());
		/*
		if(player.getEntityWorld().isRemote) {
			PacketHandler.instance.sendToServer(new AvatarUpdatePacket(player));
		}*/
	}
	
	@SubscribeEvent
	public void onPlayerLogsIn(PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		PacketHandler.instance.sendToAll(new AvatarUpdatePacket(player));
	}
	 
	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		EntityPlayer player = event.player;
		PacketHandler.instance.sendToAll(new AvatarUpdatePacket(player));
	}
	 
	@SubscribeEvent
	public void onPlayerChangeDimesion(PlayerChangedDimensionEvent event) {
		EntityPlayer player = event.player;
		PacketHandler.instance.sendToAll(new AvatarUpdatePacket(player));
	}
	 
	@SubscribeEvent
	public void onPlayerTracking(PlayerEvent.StartTracking event) {
		Entity target = event.getTarget();
		if(!(target instanceof EntityPlayer) || (target instanceof IEntityLittleMaidAvatar))return;
		EntityPlayer player= event.getEntityPlayer();
		if(player instanceof IEntityLittleMaidAvatar)return;
		EntityPlayer targetPlayer = (EntityPlayer) target;
		PacketHandler.instance.sendToAllTracking(new AvatarUpdatePacket(targetPlayer), targetPlayer);
		
	}
	
	@SubscribeEvent
	public void onPlayerDeath(PlayerEvent.Clone event) {
		IMaidAvatar oldAvatar = event.getOriginal().getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
		IMaidAvatar newAvatar = event.getEntityPlayer().getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
		newAvatar.setIsAvatarEnable(oldAvatar.getIsAvatarEnable());
		newAvatar.setAvatarModel(oldAvatar.getMainModel(), oldAvatar.getModelColor(), oldAvatar.getArmorModel());
	}
}
