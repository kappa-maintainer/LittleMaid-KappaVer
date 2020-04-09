package net.firis.lmt.proxy;

import net.firis.lmt.handler.CommonHandler;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy{
		@Override
		public void init() {
			MinecraftForge.EVENT_BUS.register(new CommonHandler());
			super.init();
		}

}
