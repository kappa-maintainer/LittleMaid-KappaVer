package net.blacklab.lib.minecraft.item;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemUtil {
	
	public static Item getItemByStringId(String id){
		Item item = Item.REGISTRY.getObject(new ResourceLocation(id));
		return item;
	}

	public static boolean isHelm(ItemStack stack){
		if(stack!=null){
			if(stack.getItem() instanceof ItemArmor){
				if(((ItemArmor)stack.getItem()).armorType == EntityEquipmentSlot.HEAD){
					return true;
				}
			}
		}
		return false;
	}
	
	public static int getFoodAmount(ItemStack pStack) {
		if (pStack == null) {
			return -1;
		}
		if (pStack.getItem() instanceof ItemFood) {
			return ((ItemFood) pStack.getItem()).getHealAmount(pStack);
		}
		return -1;
	}
}
