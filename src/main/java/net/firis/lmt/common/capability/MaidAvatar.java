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
	private boolean isSitting = false;
	private boolean isWaiting =false;
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

	@Override
	public void setMainModel(String maidModel) {
		this.maidModel = maidModel;
		
	}

	@Override
	public void setColor(int maidColor) {
		this.maidColor = maidColor;
		
	}

	@Override
	public void setArmorModel(String maidArmor) {
		this.maidArmor = maidArmor;
		
	}

	@Override
	public void setIsSitting(boolean isSitting) {
		this.isSitting = isSitting;
		
	}

	@Override
	public boolean getIsSitting() {
		return isSitting;
	}

	@Override
	public void setIsWaiting(boolean isWaiting) {
		this.isWaiting = isWaiting;
		
	}

	@Override
	public boolean getIsWaiting() {
		return isWaiting;
	}

}
