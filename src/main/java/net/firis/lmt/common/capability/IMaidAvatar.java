/**
 * 
 */
package net.firis.lmt.common.capability;

import net.minecraft.inventory.EntityEquipmentSlot;

/**
 * Interface of player's maid avatar handler.
 *
 */
public interface IMaidAvatar {
	public void setAvatarModel(String maidModel, int maidColor, String maidArmor);
	public void setIsAvatarEnable(boolean isEnable);
	public String getMainModel();
	public int getModelColor();
	public String getArmorModel();
	public boolean getIsAvatarEnable();
}
