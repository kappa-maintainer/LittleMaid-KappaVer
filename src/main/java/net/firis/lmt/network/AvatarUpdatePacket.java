package net.firis.lmt.network;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import net.firis.lmt.common.capability.IMaidAvatar;
import net.firis.lmt.common.capability.MaidAvatarProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class AvatarUpdatePacket implements IMessage {
	
	boolean messageValid;
	
	protected String maidModel;
	protected int maidColor;
	protected String maidArmorHead;
	protected String maidArmorChest;
	protected String maidArmorLegs;
	protected String maidArmorFeet;
	protected boolean avatarEnabled;
	
	public AvatarUpdatePacket() {
		messageValid = false;
	}
	
	public AvatarUpdatePacket(EntityPlayer player) {
		IMaidAvatar avatar = player.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
		this.maidModel = avatar.getMainModel();
		this.maidColor = avatar.getModelColor();
		this.maidArmorHead = avatar.getArmorModel(EntityEquipmentSlot.HEAD);
		this.maidArmorChest = avatar.getArmorModel(EntityEquipmentSlot.CHEST);
		this.maidArmorLegs = avatar.getArmorModel(EntityEquipmentSlot.LEGS);
		this.maidArmorFeet = avatar.getArmorModel(EntityEquipmentSlot.FEET);
		
		messageValid = true;

	}
	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			avatarEnabled = buf.readBoolean();
			maidModel = ByteBufUtils.readUTF8String(buf);
			maidColor = buf.readInt();
			maidArmorHead = ByteBufUtils.readUTF8String(buf);
			maidArmorChest = ByteBufUtils.readUTF8String(buf);
			maidArmorLegs = ByteBufUtils.readUTF8String(buf);
			maidArmorFeet = ByteBufUtils.readUTF8String(buf);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			return;
		}
		messageValid = true;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		if(!messageValid)return;
		buf.writeBoolean(avatarEnabled);
		ByteBufUtils.writeUTF8String(buf, maidModel);
		buf.writeInt(maidColor);
		ByteBufUtils.writeUTF8String(buf, maidArmorHead);
		ByteBufUtils.writeUTF8String(buf, maidArmorChest);
		ByteBufUtils.writeUTF8String(buf, maidArmorLegs);
		ByteBufUtils.writeUTF8String(buf, maidArmorFeet);

	}
	
	public static class Handler implements IMessageHandler<AvatarUpdatePacket, IMessage> {

		@Override
		public IMessage onMessage(AvatarUpdatePacket message, MessageContext ctx) {
			if (!message.messageValid && ctx.side != Side.CLIENT) return null;
			Minecraft.getMinecraft().addScheduledTask(() -> processMessage(message));
			return null;
		}
		
		void processMessage(AvatarUpdatePacket message) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			if (player == null || !player.hasCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null)) return;
			IMaidAvatar avatar = player.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
			if (avatar == null) return;
			avatar.setIsAvatarEnable(message.avatarEnabled);
			avatar.setAvatarModel(message.maidModel, message.maidColor, message.maidArmorHead, message.maidArmorChest, message.maidArmorLegs, message.maidArmorFeet);
		}
		
	}

}
