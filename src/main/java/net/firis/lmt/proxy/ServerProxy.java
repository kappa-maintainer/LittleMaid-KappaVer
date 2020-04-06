package net.firis.lmt.proxy;

import net.firis.lmt.handler.CommonHandler;
import net.minecraftforge.common.MinecraftForge;

public class ServerProxy extends CommonProxy{

	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.register(new CommonHandler());
		
	}

}
