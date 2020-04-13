package net.firis.lmt.common.item;

import java.util.List;

import javax.annotation.Nullable;

import net.blacklab.lmr.entity.littlemaid.EntityLittleMaid;
import net.firis.lmt.common.capability.IMaidAvatar;
import net.firis.lmt.common.capability.MaidAvatarProvider;
import net.firis.lmt.handler.CommonHandler;
import net.firis.lmt.network.AvatarUpdatePacket;
import net.firis.lmt.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import scala.reflect.internal.Trees.This;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LMItemPlayerMaidBook extends Item {
	
	/**
	 * コンストラクタ
	 */
	public LMItemPlayerMaidBook() {
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.MISC);
	}
	/**
	 * 左クリックからのアイテム化
	 */
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
		setDressUpPlayerFromMaid(player, entity);
		return true;
    }
	
	/**
	 * Shift＋右クリックからのアイテム化
	 */
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand)
    {
		setDressUpPlayerFromMaid(playerIn, target);
		return true;
    }
	
	/**
	 * プレイヤーがメイドさんの見た目になる
	 */
	public void setDressUpPlayerFromMaid(EntityPlayer player, Entity entity) {
		
		if (!player.world.isRemote) return;
		
		if (!(entity instanceof EntityLittleMaid)) return;
		
		//対象のメイドさんからモデル情報を取得する
		EntityLittleMaid entityMaid = (EntityLittleMaid) entity;
		
		//メイドモデル名取得
		//String maidModelName = enityMaid.getTextureBox()[0].textureName;
		//String armorModelName = enityMaid.getTextureBox()[1].textureName;
		String maidModelName = enityMaid.getTextureNameMain();
		String armorModelName = enityMaid.getTextureNameArmor();
		int maidColor = enityMaid.getColor();
		IMaidAvatar avatar = player.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
		avatar.setIsAvatarEnable(true);
		avatar.setAvatarModel(maidModelName, maidColor, armorModelName, armorModelName, armorModelName, armorModelName);
		//PacketHandler.instance.sendToAll(new AvatarUpdatePacket(player));
	}
	@Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        
		if(!playerIn.isSneaking()) {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
		}
		IMaidAvatar avatar = playerIn.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
		avatar.setIsAvatarEnable(!avatar.getIsAvatarEnable());
		if(avatar.getIsAvatarEnable() == false) {
			playerIn.eyeHeight = playerIn.getDefaultEyeHeight();
	        float f;
	        float f1;

	        if (playerIn.isElytraFlying())
	        {
	            f = 0.6F;
	            f1 = 0.6F;
	        }
	        else if (playerIn.isPlayerSleeping())
	        {
	            f = 0.2F;
	            f1 = 0.2F;
	        }
	        else if (playerIn.isSneaking())
	        {
	            f = 0.6F;
	            f1 = 1.65F;
	        }
	        else
	        {
	            f = 0.6F;
	            f1 = 1.8F;
	        }
	        AxisAlignedBB box = playerIn.getEntityBoundingBox();
			playerIn.setEntityBoundingBox(new AxisAlignedBB(playerIn.posX - f/2, box.minY, playerIn.posZ - f/2, playerIn.posX + f/2, box.minY + f1, playerIn.posZ + f/2));
		}
		//PacketHandler.instance.sendToAll(new AvatarUpdatePacket(playerIn));
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.format("item.player_maid_book.info"));
    }
}
