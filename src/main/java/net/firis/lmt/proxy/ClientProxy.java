package net.firis.lmt.proxy;

import net.firis.lmt.handler.ClientHandler;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy{
		@Override
		public void init() {
			super.init();
			MinecraftForge.EVENT_BUS.register(new ClientHandler());
		}

}
