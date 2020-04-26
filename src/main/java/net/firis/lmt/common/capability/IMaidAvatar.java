/**
 * 
 */
package net.firis.lmt.common.capability;

/**
 * Interface of player's maid avatar handler.
 *
 */
public interface IMaidAvatar {
	public void setIsAvatarEnable(boolean isEnable);
	public void setMainModel(String maidModel);
	public void setColor(int maidColor);
	public void setArmorModel(String maidArmor);
	public void setIsSitting(boolean isSitting);
	public void setIsWaiting(boolean isWaiting);
	public void setWaitCounter(int counter);
	public String getMainModel();
	public int getModelColor();
	public String getArmorModel();
	public boolean getIsAvatarEnable();
	public boolean getIsSitting();
	public boolean getIsWaiting();
	public int getWaitCounter();
}
