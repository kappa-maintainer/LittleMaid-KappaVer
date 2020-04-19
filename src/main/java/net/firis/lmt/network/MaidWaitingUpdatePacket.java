package net.firis.lmt.network;

import io.netty.buffer.ByteBuf;
import net.firis.lmt.client.event.LittleMaidAvatarClientTickEventHandler;
import net.firis.lmt.common.capability.IMaidAvatar;
import net.firis.lmt.common.capability.MaidAvatarProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MaidWaitingUpdatePacket implements IMessage {

	boolean valid;
	protected int playerID;
	protected boolean isWaiting;
	public MaidWaitingUpdatePacket() {
		valid = false;
	}
	
	public MaidWaitingUpdatePacket(int playerID, boolean isEnabled) {
		this.playerID = playerID;
		this.isWaiting = isEnabled;
		valid = true;
	}
	
	public MaidWaitingUpdatePacket(EntityPlayer player) {
		if(player == null)return;
		if(!player.hasCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null))return;
		IMaidAvatar avatar = player.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
		this.playerID = player.getEntityId();
		this.isWaiting = avatar.getIsWaiting();
		valid = true;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			playerID = buf.readInt();
			isWaiting = buf.readBoolean();
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
		buf.writeBoolean(isWaiting);

	}
	
	public static class Handler implements IMessageHandler<MaidWaitingUpdatePacket, IMessage> {
		@Override
		public IMessage onMessage(MaidWaitingUpdatePacket message, MessageContext ctx) {
			if (!message.valid) return null;
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> processMessage(message, ctx));
			if(ctx.side == Side.SERVER){
				PacketHandler.instance.sendToAll(message);
			}
			return null;
		}
		
		void processMessage(MaidWaitingUpdatePacket message, MessageContext ctx) {
			Entity player;
			if(ctx.side == Side.SERVER) {
				player = ctx.getServerHandler().player;
			} else {
				player = Minecraft.getMinecraft().world.getEntityByID(message.playerID);
			}
			if (!(player instanceof EntityPlayer) || player == null || !player.hasCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null)) return;
			IMaidAvatar avatar = player.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
			if (avatar == null) return;
			avatar.setIsWaiting(message.isWaiting);
			if(ctx.side == Side.CLIENT) {
				LittleMaidAvatarClientTickEventHandler.lmAvatarWaitAction.setStat((EntityPlayer) player, message.isWaiting);
			}
		}
		
	}
}
