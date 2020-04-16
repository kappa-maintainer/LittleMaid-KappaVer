package net.firis.lmt.common.capability;
/**
 * 
 * Default
 */
public class MaidAvatar implements IMaidAvatar {
	private String maidModel = "";
	private int maidColor = -1;
	private String maidArmor = "";
	private boolean avatarEnabled = false;
	@Override
	public void setAvatarModel(String maidModel, int maidColor, String maidArmor) {
		this.maidModel = maidModel;
		this.maidColor = maidColor;
		this.maidArmor = maidArmor;

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
	public String getArmorModel() {
		return maidArmor;
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
