package net.blacklab.lmc.common.helper;

import net.blacklab.lmr.entity.littlemaid.EntityLittleMaid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class LittleMaidHelper {

	
	/**
	 * メイドさんからスポーンアイテムを生成
	 */
	@SuppressWarnings("deprecation")
	public static ItemStack getItemStackFromEntity(EntityLiving entityliving, ItemStack spawnItem) {
		
		ItemStack stack = ItemStack.EMPTY;
		
		//メイドさん以外は何もしない
		if (!(entityliving instanceof EntityLittleMaid)) return stack;
		
		stack = spawnItem;

		//EntityLivingをNBT化
		NBTTagCompound entityNBT = getNBTTagFromEntityLiving(entityliving);

		NBTTagCompound stackNBT = new NBTTagCompound();
		stackNBT.setTag("Mob", entityNBT);
		
		stack.setTagCompound(stackNBT);
		
		//Tooltip表示用設定追加
		EntityLittleMaid entityMaid = (EntityLittleMaid)entityliving;
		//オーナー名
		EntityPlayer player = (EntityPlayer) entityMaid.getOwner();
		if (player != null) {
			String playerName = player.getName();
			stack.getTagCompound().setString("maid_owner", playerName);
		}
		
		//メイド名
		stack.getTagCompound().setString("maid_name", entityMaid.getName());
		
		return stack;
	}
	
	/**
	 * メイドさんNBTからスポーンアイテムを生成
	 */
	public static ItemStack getItemStackFromNBT(NBTTagCompound entityNBT, ItemStack spawnItem) {
		
		ItemStack stack = spawnItem;
		
		NBTTagCompound stackNBT = new NBTTagCompound();
		stackNBT.setTag("Mob", entityNBT);
		
		stack.setTagCompound(stackNBT);
		
		/*
		//Tooltip表示用設定追加
		EntityLittleMaid entityMaid = (EntityLittleMaid)entityliving;
		//オーナー名
		EntityPlayer player = (EntityPlayer) entityMaid.getOwner();
		if (player != null) {
			String playerName = player.getName();
			stack.getTagCompound().setString("maid_owner", playerName);
		}
		
		//メイド名
		stack.getTagCompound().setString("maid_name", entityMaid.getName());
		*/
		
		return stack;
	}
	
	/**
	 * ItemStackからメイドさんをスポーンさせる
	 */
	public static Entity spawnEntityFromItemStack(ItemStack stack, World world, double x, double y, double z) {
		
		if (stack.hasTagCompound() == false
				|| !stack.getTagCompound().hasKey("Mob")) return null;
		
		NBTTagCompound entityNBT = (NBTTagCompound) stack.getTagCompound().getTag("Mob");
		
		Entity entity = EntityList.createEntityFromNBT(entityNBT, world);

		if (entity != null) {
			entity.setLocationAndAngles(x, y, z, 0,0 );
			if (!world.isRemote) {
				world.spawnEntity(entity);
			}
		}
		return entity;
	}
	
	
	/**
	 * NBTDoubleのTagListを生成する
	 * @param numbers
	 * @return
	 */
	protected static NBTTagList newDoubleNBTList(double... numbers) {
		NBTTagList nbttaglist = new NBTTagList();
		for (double number : numbers) {
			nbttaglist.appendTag(new NBTTagDouble(number));
		}
		return nbttaglist;
	}
	
	/**
	 * EntityLivingからMobIdを取得する
	 */
	public static String getEntityId(EntityLiving entityliving) {
		String mobid = "";
		EntityEntry entry = 
				EntityRegistry.getEntry(entityliving.getClass());
		if (entry != null) {
			mobid = entry.getRegistryName().toString();
		}
		return mobid;
	}
	
	/**
	 * EntityLivingからNBTを生成する
	 * @return
	 */
	public static NBTTagCompound getNBTTagFromEntityLiving(EntityLiving entityliving) {
		
		//NBTタグ生成
		NBTTagCompound entityNBT = new NBTTagCompound();
		entityliving.writeToNBT(entityNBT);
    	
    	//一部パラメータをリセット
    	//被ダメ無敵時間
		entityNBT.setShort("HurtTime", (short) 0);
		//モーメント
		entityNBT.setTag("Motion", newDoubleNBTList(0.0D, 0.0D, 0.0D));
		//落下距離
		entityNBT.setFloat("FallDistance", 0.0F);
		//EntityId
		entityNBT.setString("id", getEntityId(entityliving));
		
		return entityNBT;
	}
}
