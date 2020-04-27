package net.blacklab.lmr.util.helper;

import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.blacklab.lmr.LittleMaidReengaged;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class CommonHelper {

	public static final boolean isClient;
	public static final Minecraft mc;

	static {
		Minecraft lm = null;
		try {
			lm =  Minecraft.getMinecraft();// ModLoader.getMinecraftInstance();
		} catch (Exception e) {
//			e.printStackTrace();
		} catch (Error e) {
//			e.printStackTrace();
		}
		mc = lm;
		isClient = mc != null;
	}

	public static UUID getPlayerUUID(EntityPlayer par1EntityPlayer) {
		return par1EntityPlayer.getUniqueID();
	}

	/**
	 * 現在の実行環境がローカルかどうかを判定する。
	 */
	public static boolean isLocalPlay() {
		return isClient && mc.isIntegratedServerRunning();
	}

	/**
	 * アイテムに追加効果が在るかを判定する。
	 * Forge対策。
	 * @param pItemStack
	 * @return
	 */
	public static boolean hasEffect(ItemStack pItemStack) {
		// マジClientSIDEとか辞めてほしい。
		if (!pItemStack.isEmpty()) {
			Item litem = pItemStack.getItem();
			if (litem instanceof ItemPotion) {
				List<PotionEffect> llist = PotionUtils.getEffectsFromStack(pItemStack);
				return llist != null && !llist.isEmpty();
			}
		}
		return false;
	}

	/**
	 *  アイテムに設定された攻撃力を見る
	 * @param pItemStack
	 * @return
	 */
	public static double getAttackVSEntity(ItemStack pItemStack) {
		AttributeModifier lam = (AttributeModifier)pItemStack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
		return lam == null ? 0 : lam.getAmount();
	}

	public static GameProfile newGameProfile(String UUIDid, String name) {
		return new GameProfile(UUID.randomUUID(), name);
	}

	public static void notifyAdmins(ICommandSender sender, ICommand cmd, int p_152374_2_, String s, Object ... p_152374_4_) {
		CommandBase.notifyCommandListener(sender, cmd, p_152374_2_, s, p_152374_4_);
	}

	public static boolean setPathToTile(EntityLiving pEntity, TileEntity pTarget, boolean flag) {
		// Tileまでのパスを作る
		PathNavigate lpn = pEntity.getNavigator();
		float lspeed = 1.0F;
		// 向きに合わせて距離を調整
		int i = (pTarget.getPos().getY() == MathHelper.floor(pEntity.posY) && flag) ? 2 : 1;
		try {
			switch (pEntity.getEntityWorld().getBlockState(pTarget.getPos()).getValue(BlockHorizontal.FACING)) {
			case SOUTH:
				return lpn.tryMoveToXYZ(pTarget.getPos().getX(), pTarget.getPos().getY(), pTarget.getPos().getZ() + i, lspeed);
			case NORTH:
				return lpn.tryMoveToXYZ(pTarget.getPos().getX(), pTarget.getPos().getY(), pTarget.getPos().getZ(), lspeed);
			case EAST:
				return lpn.tryMoveToXYZ(pTarget.getPos().getX() + 1, pTarget.getPos().getY(), pTarget.getPos().getZ(), lspeed);
			case WEST:
				return lpn.tryMoveToXYZ(pTarget.getPos().getX() - i, pTarget.getPos().getY(), pTarget.getPos().getZ(), lspeed);
			default:
				break;
			}
		} catch (IllegalArgumentException exception) {
			LittleMaidReengaged.Debug("Failed to get direction of tile. Maybe non-vanilla chest?");
		}
		return lpn.tryMoveToXYZ(pTarget.getPos().getX(), pTarget.getPos().getY(), pTarget.getPos().getZ(), lspeed);

	}

	/**
		 * 視線の先にいる最初のEntityを返す
		 * @param pEntity
		 * 視点
		 * @param pRange
		 * 視線の有効距離
		 * @param pDelta
		 * 時刻補正
		 * @param pExpand
		 * 検知領域の拡大範囲
		 * @return
		 */
		public static Entity getRayTraceEntity(EntityLivingBase pEntity, double pRange, float pDelta, float pExpand) {
			Vec3d lvpos = new Vec3d(
					pEntity.posX, pEntity.posY + pEntity.getEyeHeight(), pEntity.posZ);
	//		Vec3 lvpos = pEntity.getPosition(pDelta).addVector(0D, pEntity.getEyeHeight(), 0D);
			Vec3d lvlook = pEntity.getLook(pDelta);
			Vec3d lvview = lvpos.addVector(lvlook.x * pRange, lvlook.y * pRange, lvlook.z * pRange);
			Entity ltarget = null;
			List<Entity> llist = pEntity.getEntityWorld()
					.getEntitiesWithinAABBExcludingEntity(pEntity, pEntity.getEntityBoundingBox().contract(lvlook.x * pRange, lvlook.y * pRange, lvlook.z * pRange).grow(pExpand, pExpand, pExpand));
			double ltdistance = pRange * pRange;

			for (int var13 = 0; var13 < llist.size(); ++var13) {
				Entity lentity = llist.get(var13);

				if (lentity.canBeCollidedWith()) {
					float lexpand = lentity.getCollisionBorderSize() + 0.3F;
					AxisAlignedBB laabb = lentity.getEntityBoundingBox().grow(lexpand, lexpand, lexpand);
					RayTraceResult lmop = laabb.calculateIntercept(lvpos, lvview);

					if (laabb.contains(lvpos)) {
						if (0.0D < ltdistance || ltdistance == 0.0D) {
							ltarget = lentity;
							ltdistance = 0.0D;
						}
					} else if (lmop != null) {
						double ldis = lvpos.squareDistanceTo(lmop.hitVec);

						if (ldis < ltdistance || ltdistance == 0.0D) {
							ltarget = lentity;
							ltdistance = ldis;
						}
					}
				}
			}
			return ltarget;
		}

	public static String getDeadSource(DamageSource source) {
		String ls = source.getDamageType();
	
		Entity lentity = source.getTrueSource();
		if (lentity != null) {
			if (lentity instanceof EntityPlayer) {
				ls += ":" + lentity.getName();
			} else {
				String lt = EntityList.getEntityString(lentity);
				if (lt != null) {
					ls += ":" + lt;
				}
			}
		}
	
		return ls;
	}

}
