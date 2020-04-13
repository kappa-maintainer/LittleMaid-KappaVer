package net.firis.lmt.proxy;

import net.blacklab.lmr.config.LMRConfig;
import net.firis.lmt.common.capability.IMaidAvatar;
import net.firis.lmt.common.capability.MaidAvatar;
import net.firis.lmt.common.capability.PlayerAvatarStorage;
import net.firis.lmt.handler.MaidAvatarCapabilityHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CommonProxy {
	public void init() {
		if(LMRConfig.cfg_prottype_maid_avatar) {
			CapabilityManager.INSTANCE.register(IMaidAvatar.class, new PlayerAvatarStorage(), MaidAvatar::new);
			MinecraftForge.EVENT_BUS.register(new MaidAvatarCapabilityHandler());
		}
	}
}
