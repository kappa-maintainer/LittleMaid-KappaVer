package net.firis.lmt.common.capability;

import net.firis.lmt.config.FirisConfig;
import net.minecraft.inventory.EntityEquipmentSlot;

public class MaidAvatar implements IMaidAvatar {
	private String maidModel = NO_AVATAR;
	private int maidColor = -1;
	private String maidArmorHead = NO_AVATAR;
	private String maidArmorChest = NO_AVATAR;
	private String maidArmorLegs = NO_AVATAR;
	private String maidArmorFeet = NO_AVATAR;
	@Override
	public void setAvatarModel(String maidModel, int maidColor, String maidArmorHead, String maidArmorChest,
			String maidArmorLegs, String maidArmorFeet) {
		this.maidModel = maidModel;
		this.maidColor = maidColor;
		this.maidArmorHead = maidArmorHead;
		this.maidArmorChest = maidArmorChest;
		this.maidArmorLegs = maidArmorLegs;
		this.maidArmorFeet = maidArmorFeet;

	}

	@Override
	public String getMainModel() {
		return maidModel;
	}

	@Override
	public int getModelColor() {
		return maidColor;
	}

	@Override
	public String getArmorModel(EntityEquipmentSlot slot) {
		switch (slot) {
		case HEAD:
			return maidArmorHead;
		case CHEST:
			return maidArmorChest;
		case LEGS:
			return maidArmorLegs;
		case FEET:
			return maidArmorLegs;
		default:
			return NO_AVATAR;
		}
	}

}
