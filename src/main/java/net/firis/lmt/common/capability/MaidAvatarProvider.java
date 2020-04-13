package net.firis.lmt.common.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class MaidAvatarProvider implements ICapabilitySerializable<NBTBase> {
	@CapabilityInject(IMaidAvatar.class)
	
	public static final Capability<IMaidAvatar> MAID_AVATAR_CAPABILITY = null;
	
	private IMaidAvatar instance = MAID_AVATAR_CAPABILITY.getDefaultInstance();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == MAID_AVATAR_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == MAID_AVATAR_CAPABILITY?MAID_AVATAR_CAPABILITY.cast(this.instance):null;
	}

	@Override
	public NBTBase serializeNBT() {
		return MAID_AVATAR_CAPABILITY.getStorage().writeNBT(MAID_AVATAR_CAPABILITY, instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		MAID_AVATAR_CAPABILITY.getStorage().readNBT(MAID_AVATAR_CAPABILITY, instance, null, nbt);		
	}

}
