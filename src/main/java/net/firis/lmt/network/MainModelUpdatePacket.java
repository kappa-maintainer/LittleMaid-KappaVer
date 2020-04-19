package net.firis.lmt.network;

import io.netty.buffer.ByteBuf;
import net.firis.lmt.common.DefaultBoxSwitcher;
import net.firis.lmt.common.capability.IMaidAvatar;
import net.firis.lmt.common.capability.MaidAvatarProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MainModelUpdatePacket implements IMessage {

	boolean valid;
	protected int playerID;
	protected String mainModel;
	public MainModelUpdatePacket() {
		valid = false;
	}
	
	public MainModelUpdatePacket(int playerID, String mainModel) {
		this.mainModel = mainModel;
		this.playerID = playerID;
		valid = true;
	}
	
	public MainModelUpdatePacket(EntityPlayer player) {
		if(player == null)return;
		if(!player.hasCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null))return;
		IMaidAvatar avatar = player.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
		this.mainModel = avatar.getMainModel();
		this.playerID = player.getEntityId();
		valid = true;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			playerID = buf.readInt();
			mainModel = ByteBufUtils.readUTF8String(buf);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			return;
		}
		valid = true;

	}

	@Override
	public void toBytes(ByteBuf buf) {
		if(!valid)return;
		buf.writeInt(playerID);
		ByteBufUtils.writeUTF8String(buf, mainModel);

	}
	
	public static class Handler implements IMessageHandler<MainModelUpdatePacket, IMessage> {
		@Override
		public IMessage onMessage(MainModelUpdatePacket message, MessageContext ctx) {
			if (!message.valid) return null;
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> processMessage(message, ctx));
			if(ctx.side == Side.SERVER){
				PacketHandler.instance.sendToAll(message);
			}
			return null;
		}
		
		void processMessage(MainModelUpdatePacket message, MessageContext ctx) {
			Entity player;
			if(ctx.side == Side.SERVER) {
				player = ctx.getServerHandler().player;
			} else {
				player = Minecraft.getMinecraft().world.getEntityByID(message.playerID);
			}
			if (!(player instanceof EntityPlayer) || player == null || !player.hasCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null)) return;
			IMaidAvatar avatar = player.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
			if (avatar == null) return;
			avatar.setMainModel(message.mainModel);
		}
		
	}

}
