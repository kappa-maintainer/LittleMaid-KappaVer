package net.firis.lmt.common.capability;

import net.firis.lmt.config.FirisConfig;
import net.minecraft.inventory.EntityEquipmentSlot;

public class MaidAvatar implements IMaidAvatar {
	private String maidModel = DEFAULT_MAID_MODEL;
	private int maidColor = 0;
	private String maidArmorHead = DEFAULT_MAID_MODEL;
	private String maidArmorChest = DEFAULT_MAID_MODEL;
	private String maidArmorLegs = DEFAULT_MAID_MODEL;
	private String maidArmorFeet = DEFAULT_MAID_MODEL;
	private boolean avatarEnabled = false;
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
			return DEFAULT_MAID_MODEL;
		}
	}

	@Override
	public void setIsAvatarEnable(boolean isEnable) {
		avatarEnabled = isEnable;
	}

	@Override
	public boolean getIsAvatarEnable() {
		return avatarEnabled;
	}

}
