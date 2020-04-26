package net.firis.lmt.handler;


import net.blacklab.lmr.LittleMaidReengaged;
import net.firis.lmt.common.capability.IMaidAvatar;
import net.firis.lmt.common.capability.MaidAvatarProvider;
import net.firis.lmt.network.ArmorModelUpdatePacket;
import net.firis.lmt.network.AvatarSwitchPacket;
import net.firis.lmt.network.MaidColorUpdatePacket;
import net.firis.lmt.network.MaidSittingUpdatePacket;
import net.firis.lmt.network.MaidWaitingUpdatePacket;
import net.firis.lmt.network.MainModelUpdatePacket;
import net.firis.lmt.network.PacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
		if(!(event.getObject() instanceof EntityPlayer) || (event.getObject() instanceof FakePlayer))return;
		event.addCapability(MAID_AVATAR_CAPABILITY, new MaidAvatarProvider());
		/*
		if(player.getEntityWorld().isRemote) {
			PacketHandler.instance.sendToServer(new AvatarUpdatePacket(player));
		}*/
	}
	
	@SubscribeEvent
	public void onPlayerLogsIn(PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		//PacketHandler.instance.sendToAll(new AvatarUpdatePacket(player));
		PacketHandler.instance.sendToAll(new AvatarSwitchPacket(player));
		PacketHandler.instance.sendToAll(new MainModelUpdatePacket(player));
		PacketHandler.instance.sendToAll(new ArmorModelUpdatePacket(player));
		PacketHandler.instance.sendToAll(new MaidColorUpdatePacket(player));
		PacketHandler.instance.sendToAll(new MaidWaitingUpdatePacket(player));
		PacketHandler.instance.sendToAll(new MaidSittingUpdatePacket(player));
	}
	 
	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		EntityPlayer player = event.player;
		//PacketHandler.instance.sendToAll(new AvatarUpdatePacket(player));
		PacketHandler.instance.sendToAll(new AvatarSwitchPacket(player));
		PacketHandler.instance.sendToAll(new MainModelUpdatePacket(player));
		PacketHandler.instance.sendToAll(new ArmorModelUpdatePacket(player));
		PacketHandler.instance.sendToAll(new MaidColorUpdatePacket(player));
		PacketHandler.instance.sendToAll(new MaidWaitingUpdatePacket(player));
		PacketHandler.instance.sendToAll(new MaidSittingUpdatePacket(player));
	}
	 
	@SubscribeEvent
	public void onPlayerChangeDimesion(PlayerChangedDimensionEvent event) {
		EntityPlayer player = event.player;
		//PacketHandler.instance.sendToAll(new AvatarUpdatePacket(player));
		PacketHandler.instance.sendToAll(new AvatarSwitchPacket(player));
		PacketHandler.instance.sendToAll(new MainModelUpdatePacket(player));
		PacketHandler.instance.sendToAll(new ArmorModelUpdatePacket(player));
		PacketHandler.instance.sendToAll(new MaidColorUpdatePacket(player));
		PacketHandler.instance.sendToAll(new MaidWaitingUpdatePacket(player));
		PacketHandler.instance.sendToAll(new MaidSittingUpdatePacket(player));
	}
	 
	@SubscribeEvent
	public void onPlayerTracking(PlayerEvent.StartTracking event) {
		Entity target = event.getTarget();
		if(!(target instanceof EntityPlayer) || (target instanceof FakePlayer))return;
		EntityPlayer player= event.getEntityPlayer();
		if(player instanceof FakePlayer)return;
		EntityPlayer targetPlayer = (EntityPlayer) target;
		//PacketHandler.instance.sendToAllTracking(new AvatarUpdatePacket(targetPlayer), targetPlayer);
		
		PacketHandler.instance.sendToAll(new AvatarSwitchPacket(targetPlayer));
		PacketHandler.instance.sendToAll(new MainModelUpdatePacket(targetPlayer));
		PacketHandler.instance.sendToAll(new ArmorModelUpdatePacket(targetPlayer));
		PacketHandler.instance.sendToAll(new MaidColorUpdatePacket(targetPlayer));
		PacketHandler.instance.sendToAll(new MaidWaitingUpdatePacket(targetPlayer));
		PacketHandler.instance.sendToAll(new MaidSittingUpdatePacket(targetPlayer));
		
	}
	
	@SubscribeEvent
	public void onPlayerDeath(PlayerEvent.Clone event) {
		IMaidAvatar oldAvatar = event.getOriginal().getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
		IMaidAvatar newAvatar = event.getEntityPlayer().getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
		newAvatar.setIsAvatarEnable(oldAvatar.getIsAvatarEnable());
		newAvatar.setMainModel(oldAvatar.getMainModel());
		newAvatar.setColor(oldAvatar.getModelColor());
		newAvatar.setArmorModel(oldAvatar.getArmorModel());
		newAvatar.setIsSitting(oldAvatar.getIsSitting());
		newAvatar.setIsWaiting(oldAvatar.getIsWaiting());
	}
}
