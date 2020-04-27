package net.blacklab.lmr.entity.ai;

import net.blacklab.lmr.config.LMRConfig;
import net.blacklab.lmr.entity.littlemaid.EntityLittleMaid;
import net.blacklab.lmr.util.helper.MaidHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;

public class EntityAILMFollowOwner extends EntityAIBase implements IEntityAILM {

	private EntityLittleMaid theMaid;
	private Entity theOwner;
	private float moveSpeed;
	//private PathNavigate petPathfinder;
	private int field_48310_h;
	//private double maxDist;
	//private double minDist;
	protected double sprintDist;
	protected boolean isEnable;

	public EntityAILMFollowOwner(EntityLittleMaid par1EntityLittleMaid,
			float pSpeed, double pSprintDistSQ) {
		theMaid = par1EntityLittleMaid;
		moveSpeed = pSpeed;
		//petPathfinder = par1EntityLittleMaid.getNavigator();
		sprintDist = pSprintDistSQ;
		isEnable = true;
		setMutexBits(3);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		if (!isEnable)
			return false;
		return MaidHelper.canStartFollow(theMaid);
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting() {
//		if(theMaid.handleWaterMovement()) return !theMaid.isMaidWait()&&!theMaid.isSitting();
		return !theMaid.getNavigator().noPath()
				&& shouldExecute();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		theOwner = theMaid.getMaidMasterEntity();
		field_48310_h = 0;
		//lastAvoidWater = petPathfinder.getAvoidsWater();
		//petPathfinder.setAvoidsWater(false);
//		if(!theMaid.isInWater()) ((PathNavigateGround)this.theMaid.getNavigator()).setAvoidsWater(false);
	}

	/**
	 * Resets the task
	 */
	@Override
	public void resetTask() {
		theMaid.setSprinting(false);
		theOwner = null;
//		if(!theMaid.isInWater()) ((PathNavigateGround)this.theMaid.getNavigator()).setAvoidsWater(true);
		theMaid.getNavigator().clearPath();
		//petPathfinder.setAvoidsWater(lastAvoidWater);
	}

	/**
	 * Updates the task
	 */
	@Override
	public void updateTask() {
		double toDistance = theMaid.getDistanceSq(theOwner);
		
		if (toDistance - theMaid.getActiveModeClass().getDistanceSqToStartFollow() > 1.0) {
			theMaid.getLookHelper().setLookPositionWithEntity(theOwner, 10F, theMaid.getVerticalFaceSpeed());
		}

		if (theMaid.isSitting()) {
			return;
		}
		
		// 指定距離以上ならダッシュ
		// 水上歩行術の場合は水中でも同じ扱いとする
		if(!theMaid.isInWater() || LMRConfig.cfg_test_water_walking){
			theMaid.setSprinting(toDistance > sprintDist);
			if (--field_48310_h > 0) {
				return;
			}
		}

		field_48310_h = 10;

		Path entity = theMaid.getNavigator().getPathToEntityLiving(theOwner);
		theMaid.getNavigator().setPath(entity, moveSpeed);
	}

	@Override
	public void setEnable(boolean pFlag) {
		isEnable = pFlag;
	}

	@Override
	public boolean getEnable() {
		return isEnable;
	}

}
