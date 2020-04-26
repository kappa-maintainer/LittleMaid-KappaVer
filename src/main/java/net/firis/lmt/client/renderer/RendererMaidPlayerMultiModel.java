package net.firis.lmt.client.renderer;


import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.firis.lmt.client.model.ModelLittleMaidMultiModel;
import net.firis.lmt.client.renderer.layer.LayerArmorLittleMaidMultiModel;
import net.firis.lmt.client.renderer.layer.LayerArrowLittleMaid;
import net.firis.lmt.client.renderer.layer.LayerCustomHeadLittleMaid;
import net.firis.lmt.client.renderer.layer.LayerElytraLittleMaid;
import net.firis.lmt.client.renderer.layer.LayerEntityOnShoulderLittleMaid;
import net.firis.lmt.client.renderer.layer.LayerHeldItemLittleMaidMultiModel;
import net.firis.lmt.common.capability.MaidAvatarProvider;
import net.firis.lmt.common.manager.PlayerModelManager;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * マルチモデルでプレイヤーモデル描画
 * @author firis-games
 *
 */
public class RendererMaidPlayerMultiModel extends RenderPlayer {
	
	protected List<LayerRenderer<AbstractClientPlayer>> maidLayerRenderers = new ArrayList();
	protected ModelBase origModelBase;
	protected ModelBase maidModelBase;
	/**
	 * コンストラクタ
	 * @param renderManagerIn
	 * @param modelBaseIn
	 * @param shadowSizeIn
	 */
	public RendererMaidPlayerMultiModel(RenderPlayer renderPlayer, boolean useSmallArms) {
		
		super(renderPlayer.getRenderManager(), useSmallArms);
		//Store default player model
		this.origModelBase = new ModelPlayer(0.0F, useSmallArms);
		//初期化
		//this.layerRenderers.clear();
		this.mainModel = new ModelLittleMaidMultiModel(shadowOpaque, useSmallArms);
		this.shadowSize = 0.5F;

		//Layerロード開始
		this.isLayerLoading = true;
		
		//layer追加
		this.addLayer(new LayerHeldItemLittleMaidMultiModel(this));
		this.addLayer(new LayerArmorLittleMaidMultiModel(this));
		
		//Player用のlayerを一部改造
        this.addLayer(new LayerElytraLittleMaid(this));
        this.addLayer(new LayerCustomHeadLittleMaid(this));
        this.addLayer(new LayerEntityOnShoulderLittleMaid(renderManager, this));
        this.addLayer(new LayerArrowLittleMaid(this));
        
        //Layer登録用イベント
   		//MinecraftForge.EVENT_BUS.post(new ClientEventLMAvatar.RendererAvatarAddLayerEvent(this));
        maidModelBase = this.mainModel;
		//Layerロード完了
		this.isLayerLoading = false;
	}
	
	/**
	 * addLayer制御用
	 * 初期化中だけロード中に設定しAddLayerを有効化する
	 */
	private boolean isLayerLoading = false;
	
	/**
	 * Use different layers for different player
	 */
	@Override
	protected void renderLayers(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn) {
		if(((EntityPlayer)entitylivingbaseIn).getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null).getIsAvatarEnable()) {
			this.mainModel = maidModelBase;
	        for (LayerRenderer<AbstractClientPlayer> layerrenderer : this.maidLayerRenderers)
	        {
	            boolean flag = this.setBrightness(entitylivingbaseIn, partialTicks, layerrenderer.shouldCombineTextures());
	            layerrenderer.doRenderLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleIn);

	            if (flag)
	            {
	                this.unsetBrightness();
	            }
	        }
		} else {
			this.mainModel = origModelBase;
			super.renderLayers(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch,
					scaleIn);
		}
	}
	
	/**
	 * レイヤー登録処理
	 * isInitLayerLoaded=trueの場合はロード済みとしてLayerの登録処理を行わない
	 * Maid's layers add to a different list
	 */
	@Override
	public <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean addLayer(U layer)
    {
		if(this.isLayerLoading) {
			return this.maidLayerRenderers.add((LayerRenderer<AbstractClientPlayer>)layer);
		} else {
			return this.layerRenderers.add((LayerRenderer<AbstractClientPlayer>)layer);
		}
	}
    
	
	/**
	 * バインド用のテクスチャ
	 */
	@Override
	public ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
		//EntityPlayerからテクスチャを取得する
		//メイドモデルの実装だとこの部分はnullを返却する
		if(entity.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null).getIsAvatarEnable())
			return PlayerModelManager.getPlayerTexture(entity);
		else {
			return super.getEntityTexture(entity);
		}
	}
	
	/**
	 * 試作段階のため通常のPlayerModelをダミーモデルとして返却する
	 */
	/*@Override
	public ModelPlayer getMainModel()
    {
        return (ModelPlayer) dummyMainModel;
    }*/
	
	@Override
	protected void renderModel(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		if(entitylivingbaseIn.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null).getIsAvatarEnable()) {
			mainModel = maidModelBase;
			super.renderModel(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
		} else {
			mainModel = origModelBase;
			super.renderModel(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
		}
	}
	
	/**
	 * model情報を取得する
	 * @return
	 */
	public ModelLittleMaidMultiModel getLittleMaidMultiModel() {
		return (ModelLittleMaidMultiModel) this.maidModelBase;
	}
	
	/**
	 * プレイヤーモデルの初期化をしてから描画処理を行う
	 */
	@Override
	public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if(entity.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null).getIsAvatarEnable()) {
			mainModel = maidModelBase;

		
			//パラメータを初期化
			((ModelLittleMaidMultiModel) this.mainModel).initPlayerModel(entity, x, y, z, entityYaw, partialTicks);
	
			//法線の再計算
			//GlStateManager.enableRescaleNormal();
			//GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glEnable(GL11.GL_NORMALIZE);
			//描画処理
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		} else {
			mainModel = origModelBase;
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}

		
	}
	
	@Override
	public void renderRightArm(AbstractClientPlayer clientPlayer) {
		if(clientPlayer.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null).getIsAvatarEnable()) {
			mainModel = maidModelBase;
			this.renderFirstPersonArm(clientPlayer);
		} else {
			mainModel = origModelBase;
			super.renderRightArm(clientPlayer);
		}
	}
	
	@Override
	public void renderLeftArm(AbstractClientPlayer clientPlayer) {
		if(clientPlayer.getCapability(MaidAvatarProvider.MAID_AVATAR_CAPABILITY, null).getIsAvatarEnable()) {
			mainModel = maidModelBase;
			this.renderFirstPersonArm(clientPlayer);
		} else {
			mainModel = origModelBase;
			super.renderLeftArm(clientPlayer);
		}
	}
	
	/**
	 * 一人称視点の手を描画する
	 */
	protected void renderFirstPersonArm(AbstractClientPlayer clientPlayer) {
		
		((ModelLittleMaidMultiModel) this.mainModel).renderFirstPersonArm(clientPlayer);
		
	}
	
}
