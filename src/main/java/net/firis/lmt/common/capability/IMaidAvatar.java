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
	public static final String DEFAULT_MAID_MODEL = "default_Orign";
	public void setAvatarModel(String maidModel, int maidColor, String maidArmorHead ,String maidArmorChest ,String maidArmorLegs ,String maidArmorFeet);
	public void setIsAvatarEnable(boolean isEnable);
	public String getMainModel();
	public int getModelColor();
	public String getArmorModel(EntityEquipmentSlot slot);
	public boolean getIsAvatarEnable();
}
