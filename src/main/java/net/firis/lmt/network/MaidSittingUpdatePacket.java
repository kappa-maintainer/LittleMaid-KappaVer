package net.firis.lmt.network;

import io.netty.buffer.ByteBuf;
import net.firis.lmt.client.event.LittleMaidAvatarClientTickEventHandler;
import net.firis.lmt.common.DefaultBoxSwitcher;
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

public class MaidSittingUpdatePacket implements IMessage {

	boolean valid;
	protected int playerID;
	protected boolean isSitting;
	public MaidSittingUpdatePacket() {
		valid = false;
	}
	
	public MaidSittingUpdatePacket(int playerID, boolean isEnabled) {
		this.playerID = playerID;
		this.isSitting = isEnabled;
		valid = true;
	}
	
	public MaidSittingUpdatePacket(EntityPlayer player) {
		if(player == null)return;
		if(!player.hasCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null))return;
		IMaidAvatar avatar = player.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
		this.playerID = player.getEntityId();
		this.isSitting = avatar.getIsSitting();
		valid = true;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			playerID = buf.readInt();
			isSitting = buf.readBoolean();
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
		buf.writeBoolean(isSitting);

	}
	
	public static class Handler implements IMessageHandler<MaidSittingUpdatePacket, IMessage> {
		@Override
		public IMessage onMessage(MaidSittingUpdatePacket message, MessageContext ctx) {
			if (!message.valid) return null;
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> processMessage(message, ctx));
			if(ctx.side == Side.SERVER){
				PacketHandler.instance.sendToAll(message);
			}
			return null;
		}
		
		void processMessage(MaidSittingUpdatePacket message, MessageContext ctx) {
			Entity player;
			if(ctx.side == Side.SERVER) {
				player = ctx.getServerHandler().player;
			} else {
				player = Minecraft.getMinecraft().world.getEntityByID(message.playerID);
			}
			if (!(player instanceof EntityPlayer) || player == null || !player.hasCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null)) return;
			IMaidAvatar avatar = player.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
			if (avatar == null) return;
			avatar.setIsSitting(message.isSitting);
			if(ctx.side == Side.CLIENT) {
				LittleMaidAvatarClientTickEventHandler.lmAvatarAction.setStat((EntityPlayer) player, message.isSitting);
			}
		}
		
	}
}
