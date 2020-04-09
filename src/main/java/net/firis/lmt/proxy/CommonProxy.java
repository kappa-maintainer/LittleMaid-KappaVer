package net.firis.lmt.proxy;

import net.firis.lmt.common.capability.IMaidAvatar;
import net.firis.lmt.common.capability.MaidAvatar;
import net.firis.lmt.common.capability.PlayerAvatarStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CommonProxy {
	public void init() {
		CapabilityManager.INSTANCE.register(IMaidAvatar.class, new PlayerAvatarStorage(), MaidAvatar::new);
	}
}
