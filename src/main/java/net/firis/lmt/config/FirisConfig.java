package net.firis.lmt.config;

import java.io.File;

import net.blacklab.lmr.LittleMaidReengaged;
import net.minecraftforge.common.config.Configuration;

public class FirisConfig {

	public static Configuration config;
	
	public static String CATEGORY_GENERAL = "General";
	public static String CATEGORY_AVATAR = "PlayerMaidAvatar";
	
	public static String DEFAULT_MAID_MODEL = "default_Orign";
	
	/**
	 * メイドさんモデル
	 */
	public static String cfg_maid_model = "";
	
	/**
	 * メイドさんのカラーインデックス
	 */
	public static Integer cfg_maid_color = 0;
	
	/**
	 * メイドさんの頭防具モデル各種類
	 */
	public static String cfg_armor_model_head = "";
	public static String cfg_armor_model_body = "";
	public static String cfg_armor_model_leg = "";
	public static String cfg_armor_model_boots = "";
	
	public static Boolean cfg_immersive_avatar = true;
	public static Boolean cfg_model_override = false;
	
	public static void init(File configDir) {
		
		File configFile = new File(configDir, "lmrfp_maidavatar.cfg");
		
		config = new Configuration(configFile, LittleMaidReengaged.VERSION, true);
		
		//カテゴリーコメントの設定
		initCategory();
		
		//設定値の同期
		syncConfig();
		
	}
	
	/**
	 * カテゴリーのコメント設定
	 */
	protected static void initCategory() {
		
		config.addCustomCategoryComment(CATEGORY_GENERAL, "メイドさんの姿になる機能");
		
		config.addCustomCategoryComment(CATEGORY_AVATAR, "マルチモデルの設定");
		
	}
	
	/**
	 * Config値の同期処理
	 */
	public static void syncConfig() {
		
		//General
		
		//--------------------------------------------------
		cfg_model_override = config.getBoolean("01.ModelOverride", CATEGORY_AVATAR, 
				false, "Force override all player models with following configs.");
		
		//PlayerMaidAvatar
		cfg_maid_model = config.getString("01-1.MaidModel", CATEGORY_AVATAR,
				DEFAULT_MAID_MODEL, 
				"メイドさんのマルチモデル名");
		
		cfg_maid_color = config.getInt("01-2.MaidColorNo", CATEGORY_AVATAR, 
				0, 0, 15, 
				"メイドさんのカラー番号");
		
		//メイドさんの防具モデル
		//頭
		cfg_armor_model_head = config.getString("01-3.ArmorHelmetModel", CATEGORY_AVATAR,
				DEFAULT_MAID_MODEL, 
				"頭防具モデル名");
		
		cfg_armor_model_body = config.getString("01-4.ArmorChestplateModel", CATEGORY_AVATAR,
				DEFAULT_MAID_MODEL, 
				"胴防具モデル名");
		
		cfg_armor_model_leg = config.getString("01-5.ArmorLeggingsModel", CATEGORY_AVATAR,
				DEFAULT_MAID_MODEL, 
				"腰防具モデル名");
		
		cfg_armor_model_boots = config.getString("01-6.ArmorBootsModel", CATEGORY_AVATAR,
				DEFAULT_MAID_MODEL, 
				"靴防具モデル名");
		
		cfg_immersive_avatar = config.getBoolean("02.ImmersiveAvatar", CATEGORY_AVATAR, 
				true, "Change avatar's bounding box and view height to maid's");
		config.save();
		
	}

}
