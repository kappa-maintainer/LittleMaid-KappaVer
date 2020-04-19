package net.firis.lmt.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
	public static SimpleNetworkWrapper instance =null;
	

	public static void registerMessages(String channelName) {
		instance = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
		registerMessages();
	}
	
	public static void registerMessages() {
		//instance.registerMessage(AvatarUpdatePacket.Handler.class, AvatarUpdatePacket.class, nextID(), Side.CLIENT);
		//instance.registerMessage(AvatarUpdatePacket.Handler.class, AvatarUpdatePacket.class, nextID(), Side.SERVER);
		instance.registerMessage(AvatarSwitchPacket.Handler.class, AvatarSwitchPacket.class, nextID(), Side.CLIENT);
		instance.registerMessage(AvatarSwitchPacket.Handler.class, AvatarSwitchPacket.class, nextID(), Side.SERVER);
		instance.registerMessage(MainModelUpdatePacket.Handler.class, MainModelUpdatePacket.class, nextID(), Side.CLIENT);
		instance.registerMessage(MainModelUpdatePacket.Handler.class, MainModelUpdatePacket.class, nextID(), Side.SERVER);
		instance.registerMessage(ArmorModelUpdatePacket.Handler.class, ArmorModelUpdatePacket.class, nextID(), Side.CLIENT);
		instance.registerMessage(ArmorModelUpdatePacket.Handler.class, ArmorModelUpdatePacket.class, nextID(), Side.SERVER);
		instance.registerMessage(MaidColorUpdatePacket.Handler.class, MaidColorUpdatePacket.class, nextID(), Side.CLIENT);
		instance.registerMessage(MaidColorUpdatePacket.Handler.class, MaidColorUpdatePacket.class, nextID(), Side.SERVER);
		instance.registerMessage(MaidSittingUpdatePacket.Handler.class, MaidSittingUpdatePacket.class, nextID(), Side.CLIENT);
		instance.registerMessage(MaidSittingUpdatePacket.Handler.class, MaidSittingUpdatePacket.class, nextID(), Side.SERVER);
		instance.registerMessage(MaidWaitingUpdatePacket.Handler.class, MaidWaitingUpdatePacket.class, nextID(), Side.CLIENT);
		instance.registerMessage(MaidWaitingUpdatePacket.Handler.class, MaidWaitingUpdatePacket.class, nextID(), Side.SERVER);
	}

	private static int packetId = 0;
	
	public static int nextID() {
		return packetId++;
	}
}
