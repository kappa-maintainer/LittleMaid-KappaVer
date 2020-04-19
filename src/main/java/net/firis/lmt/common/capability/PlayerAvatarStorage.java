package net.firis.lmt.common.capability;

import net.blacklab.lmr.LittleMaidReengaged;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PlayerAvatarStorage implements IStorage<IMaidAvatar>{

	@Override
	public NBTTagCompound writeNBT(Capability<IMaidAvatar> capability, IMaidAvatar instance, EnumFacing side) {
		NBTTagCompound modelCompound = new NBTTagCompound();
		modelCompound.setString("Main", instance.getMainModel());
		modelCompound.setInteger("Color", instance.getModelColor());
		modelCompound.setString("Armor", instance.getArmorModel());
		modelCompound.setBoolean("Enabled", instance.getIsAvatarEnable());
		modelCompound.setBoolean("Sit", instance.getIsSitting());
		modelCompound.setBoolean("Wait", instance.getIsWaiting());
		return modelCompound;
	}

	@Override
	public void readNBT(Capability<IMaidAvatar> capability, IMaidAvatar instance, EnumFacing side, NBTBase nbt) {
		NBTTagCompound modelnbt = (NBTTagCompound)nbt;
		instance.setIsAvatarEnable(modelnbt.getBoolean("Enabled"));
		instance.setMainModel(modelnbt.getString("Main"));
		instance.setColor(modelnbt.getInteger("Color"));
		instance.setArmorModel(modelnbt.getString("Armor"));
		instance.setIsSitting(modelnbt.getBoolean("Sit"));
		instance.setIsWaiting(modelnbt.getBoolean("Wait"));
	}

}
