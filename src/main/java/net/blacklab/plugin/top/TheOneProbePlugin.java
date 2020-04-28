package net.blacklab.plugin.top;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;


import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import mcjty.theoneprobe.config.Config;
import net.blacklab.lmr.LittleMaidReengaged;
import net.blacklab.lmr.config.LMRConfig;
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
		if(mode == ProbeMode.NORMAL)return;
		if(!(entity instanceof EntityLittleMaid)) return;
		EntityLittleMaid maid = (EntityLittleMaid) entity;
		if(!maid.getMaidMasterEntity().isEntityEqual(player) && !LMRConfig.cfg_cstm_everyones_maid)return;
		List<ItemStack> stacks = new ArrayList();
		for(int i = 0; i < maid.maidInventory.getSizeInventory(); i++) {
			if(!maid.maidInventory.getStackInSlot(i).isEmpty())
				stacks.add(maid.maidInventory.getStackInSlot(i).copy());
		}
		if(stacks.size() > 0) {
	        IProbeInfo vertical = null;
	        IProbeInfo horizontal = null;

	        int rows = 0;
	        int idx = 0;

	        vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().borderColor(Config.chestContentsBorderColor).spacing(0));

            for (ItemStack stackInSlot : stacks) {
                if (idx % 7 == 0) {
                    horizontal = vertical.horizontal(new LayoutStyle().spacing(0));
                    rows++;
                    if (rows > 4) {
                        break;
                    }
                }
                horizontal.item(stackInSlot);
                idx++;
            }
	        
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
