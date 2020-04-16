package net.firis.lmt.common.item;

import java.util.List;

import javax.annotation.Nullable;

import net.blacklab.lmr.config.LMRConfig;
import net.blacklab.lmr.entity.littlemaid.EntityLittleMaid;
import net.firis.lmt.common.DefaultBoxSwitcher;
import net.firis.lmt.common.capability.IMaidAvatar;
import net.firis.lmt.common.capability.MaidAvatarProvider;
import net.firis.lmt.handler.CommonHandler;
import net.firis.lmt.network.AvatarUpdatePacket;
import net.firis.lmt.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import scala.reflect.internal.Trees.This;
import net.minecraftforge.fml.relauncher.Side;
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
		if(entity instanceof EntityPlayer) {
			EntityPlayer targetPlayer = (EntityPlayer)entity;
			String mainModel = targetPlayer.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null).getMainModel();
			String armorModel = targetPlayer.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null).getArmorModel();
			player.sendMessage(new TextComponentString(mainModel + "//n" +armorModel));
		}
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

		if(!LMRConfig.cfg_cstm_everyones_maid) {
			if(!entityMaid.getMaidMasterEntity().isEntityEqual(player))
				return;
		}
		//メイドモデル名取得
		//String maidModelName = enityMaid.getTextureBox()[0].textureName;
		//String armorModelName = enityMaid.getTextureBox()[1].textureName;
		String maidModelName = entityMaid.getTextureNameMain();
		String armorModelName = entityMaid.getTextureNameArmor();
		int maidColor = entityMaid.getColor();
		IMaidAvatar avatar = player.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
		if(avatar.getIsAvatarEnable() == true &&
				avatar.getMainModel().equals(maidModelName) &&
				avatar.getModelColor() == maidColor &&
				avatar.getArmorModel().equals(armorModelName))return;
		PacketHandler.instance.sendToServer(new AvatarUpdatePacket(player.getEntityId(), maidModelName, maidColor, armorModelName, true));
	}
	@Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
		
		if(!playerIn.isSneaking() || !playerIn.world.isRemote) {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
		}
		IMaidAvatar avatar = playerIn.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null);
		if(avatar.getModelColor() < 0)return new ActionResult<ItemStack>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
		boolean current = avatar.getIsAvatarEnable();
		PacketHandler.instance.sendToServer(new AvatarUpdatePacket(playerIn.getEntityId(), avatar.getMainModel(), avatar.getModelColor(), avatar.getArmorModel(), !current));
		
		if(current == true) {
			DefaultBoxSwitcher.setDefaultBox(playerIn);
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.format("item.player_maid_book.info"));
    }
}
