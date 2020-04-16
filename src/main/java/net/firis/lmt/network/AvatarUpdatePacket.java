package net.firis.lmt.network;

import java.nio.charset.Charset;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.blacklab.lmr.LittleMaidReengaged;
import net.firis.lmt.common.DefaultBoxSwitcher;
import net.firis.lmt.common.capability.IMaidAvatar;
import net.firis.lmt.common.capability.MaidAvatarProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import scala.swing.TextComponent;

public class AvatarUpdatePacket implements IMessage {
	
	boolean messageValid;
	protected String maidModel;
	protected int maidColor;
	protected String maidArmor;
	protected boolean avatarEnabled;
	protected int playerID;
	
	public AvatarUpdatePacket() {
		messageValid = false;
	}
	
	public AvatarUpdatePacket(int playerID, String maidModel, int maidColor, String maidArmor, boolean avatarEnabled) {

		this.playerID = playerID;
		this.maidModel = maidModel;
		this.maidColor = maidColor;
		this.maidArmor = maidArmor;
		this.avatarEnabled =avatarEnabled;
		messageValid = true;

	}
	
	public AvatarUpdatePacket(EntityPlayer player) {
		if(player == null)return;
		if(!player.hasCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null))return;
		IMaidAvatar avatar = player.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
		this.playerID = player.getEntityId();
		this.maidModel = avatar.getMainModel();
		this.maidColor = avatar.getModelColor();
		this.maidArmor = avatar.getArmorModel();
		this.avatarEnabled = avatar.getIsAvatarEnable();
		messageValid = true;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			playerID = buf.readInt();
			avatarEnabled = buf.readBoolean();
			maidModel = ByteBufUtils.readUTF8String(buf);
			maidColor = buf.readInt();
			maidArmor = ByteBufUtils.readUTF8String(buf);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			return;
		}
		messageValid = true;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
		if(!messageValid)return;
		
		buf.writeInt(playerID);
		buf.writeBoolean(avatarEnabled);
		ByteBufUtils.writeUTF8String(buf, maidModel);
		buf.writeInt(maidColor);
		ByteBufUtils.writeUTF8String(buf, maidArmor);

	}
	
	public static class Handler implements IMessageHandler<AvatarUpdatePacket, IMessage> {
		@Override
		public IMessage onMessage(AvatarUpdatePacket message, MessageContext ctx) {
			if (!message.messageValid) return null;
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> processMessage(message, ctx));
			if(ctx.side == Side.CLIENT) {
			} else if(ctx.side == Side.SERVER){
				PacketHandler.instance.sendToAll(message);
			}
			return null;
		}
		
		void processMessage(AvatarUpdatePacket message, MessageContext ctx) {
			Entity player;
			if(ctx.side == Side.SERVER) {
				player = ctx.getServerHandler().player;
			} else {
				player = Minecraft.getMinecraft().world.getEntityByID(message.playerID);
			}
			//EntityPlayer player=Minecraft.getMinecraft().player;
			if (!(player instanceof EntityPlayer) || player == null || !player.hasCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null)) return;
			IMaidAvatar avatar = player.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
			if (avatar == null) return;
			if(avatar.getIsAvatarEnable() == true && message.avatarEnabled ==false) {
				avatar.setIsAvatarEnable(message.avatarEnabled);
				avatar.setAvatarModel(message.maidModel, message.maidColor, message.maidArmor);
				DefaultBoxSwitcher.setDefaultBox((EntityPlayer) player);
			} else {
				avatar.setIsAvatarEnable(message.avatarEnabled);
				avatar.setAvatarModel(message.maidModel, message.maidColor, message.maidArmor);
			}
		}
		
	}

}
