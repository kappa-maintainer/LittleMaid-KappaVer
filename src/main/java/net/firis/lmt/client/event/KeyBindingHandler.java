package net.firis.lmt.client.event;

import org.lwjgl.input.Keyboard;

import net.firis.lmt.common.capability.MaidAvatarProvider;
import net.firis.lmt.network.MaidSittingUpdatePacket;
import net.firis.lmt.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * キーボードイベント
 * @author computer
 *
 */
@SideOnly(Side.CLIENT)
public class KeyBindingHandler {

	/**
	 * Avatarアクション用
	 * defalut:＠キー
	 */
	public static final KeyBinding keyLittleMaidAvatarAction = new KeyBinding(I18n.format("key.littlemaid.avatar.action"), Keyboard.KEY_GRAVE, "advancements.root");

	/**
	 * キーバインド初期化
	 */
	public static void init() {
		ClientRegistry.registerKeyBinding(keyLittleMaidAvatarAction);
	}
	
	/**
	 * キー入力イベント
	 * @param event
	 */
	@SubscribeEvent
	public static void onKeyInputEvent(KeyInputEvent event) {
	
		if (keyLittleMaidAvatarAction.isKeyDown()) {
			//アクションの制御はすべてClient側で行う
			EntityPlayer player = Minecraft.getMinecraft().player;
			LittleMaidAvatarClientTickEventHandler.lmAvatarAction.setStat(player, true);
			if(player.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null).getIsSitting() == false)
				player.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null).setIsSitting(true);
				PacketHandler.instance.sendToServer(new MaidSittingUpdatePacket(player.getEntityId(), true));
		}
	}
	
}
