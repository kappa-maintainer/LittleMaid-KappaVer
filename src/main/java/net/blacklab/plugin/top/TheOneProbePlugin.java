package net.blacklab.plugin.top;

import java.util.function.Function;

import javax.annotation.Nullable;

import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.blacklab.lmr.LittleMaidReengaged;
import net.blacklab.lmr.entity.littlemaid.EntityLittleMaid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TheOneProbePlugin implements IProbeInfoEntityProvider  {

	@Override
	public String getID() {
		return LittleMaidReengaged.DOMAIN + "littlemaid";
	}

	@Override
	public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world,
			Entity entity, IProbeHitEntityData data) {
		if(!(entity instanceof EntityLittleMaid)) return;
		EntityLittleMaid maid = (EntityLittleMaid) entity;
		for(int i = 0; i < maid.maidInventory.getSizeInventory(); i++) {
			probeInfo.item(maid.maidInventory.getStackInSlot(i));
		}
	}
	
	public static class GetTheOneProbe implements Function<ITheOneProbe, Void> 
	{

		public static ITheOneProbe theOneProbe;

		@Nullable	
		@Override
		public Void apply (ITheOneProbe input) {

			theOneProbe = input;
			theOneProbe.registerEntityProvider(new TheOneProbePlugin());
			return null;
		}
	}
}
