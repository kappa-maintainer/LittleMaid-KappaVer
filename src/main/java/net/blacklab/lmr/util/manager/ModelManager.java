package net.blacklab.lmr.util.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.blacklab.lib.classutil.FileClassUtil;
import net.blacklab.lmr.LittleMaidReengaged;
import net.blacklab.lmr.client.resource.OldZipTexturesWrapper;
import net.blacklab.lmr.config.LMRConfig;
import net.blacklab.lmr.entity.maidmodel.IModelEntity;
import net.blacklab.lmr.entity.maidmodel.ModelMultiBase;
import net.blacklab.lmr.entity.maidmodel.TextureBox;
import net.blacklab.lmr.entity.maidmodel.TextureBoxBase;
import net.blacklab.lmr.entity.maidmodel.TextureBoxServer;
import net.blacklab.lmr.util.DevMode;
import net.blacklab.lmr.util.FileList;
import net.blacklab.lmr.util.helper.CommonHelper;
import net.blacklab.lmr.util.loader.LMMultiModelHandler;
import net.blacklab.lmr.util.loader.LMTextureHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("deprecation")
public class ModelManager {

	/**
	 * 継承クラスで置き換えることを考慮。
	 */
	public static ModelManager instance = new ModelManager();

	@Deprecated
	public static String nameTextureIndex = "config/mod_MMM_textureList.cfg";
	
	public static String defaultModelName = "Orign";

	public static final int tx_oldwild		= 0x10; //16;
	public static final int tx_oldarmor1	= 0x11; //17;
	public static final int tx_oldarmor2	= 0x12; //18;
	public static final int tx_oldeye		= 0x13; //19;
	public static final int tx_gui			= 0x20; //32;
	public static final int tx_wild			= 0x30; //48;
	public static final int tx_armor1		= 0x40; //64;
	public static final int tx_armor2		= 0x50; //80;
	public static final int tx_eye			= 0x60; //96;
	public static final int tx_eyecontract	= 0x60; //96;
	public static final int tx_eyewild		= 0x70; //112;
	public static final int tx_armor1light	= 0x80; //128;
	public static final int tx_armor2light	= 0x90; //144;
	
	public static String[] armorFilenamePrefix = new String[]{
			"leather", "chainmail", "iron", "diamond", "gold"};
	
	/**
	 * 旧タイプのファイル名
	 */
	@Deprecated
	protected static String defNames[] = {
		"mob_littlemaid0.png", "mob_littlemaid1.png",
		"mob_littlemaid2.png", "mob_littlemaid3.png",
		"mob_littlemaid4.png", "mob_littlemaid5.png",
		"mob_littlemaid6.png", "mob_littlemaid7.png",
		"mob_littlemaid8.png", "mob_littlemaid9.png",
		"mob_littlemaida.png", "mob_littlemaidb.png",
		"mob_littlemaidc.png", "mob_littlemaidd.png",
		"mob_littlemaide.png", "mob_littlemaidf.png",
		"mob_littlemaidw.png",
		"mob_littlemaid_a00.png", "mob_littlemaid_a01.png"
	};

	/**
	 * ローカルで保持しているモデルのリスト
	 */
	protected Map<String, ModelMultiBase[]> modelMap = new TreeMap<String, ModelMultiBase[]>();
	
	/**
	 * ローカルで保持しているテクスチャパック
	 */
	private List<TextureBox> textures = new ArrayList<TextureBox>();
	
	/**
	 * サーバー側での管理番号を識別するのに使う、クライアント用。
	 */
	@Deprecated
	public Map<TextureBox, Integer> textureServerIndex = new HashMap<TextureBox, Integer>();
	
	/**
	 * サーバー・クライアント間でテクスチャパックの名称リストの同期を取るのに使う、サーバー用。
	 */
	@Deprecated
	public List<TextureBoxServer> textureServer = new ArrayList<TextureBoxServer>();
	/**
	 * Entity毎にデフォルトテクスチャを参照。
	 * 構築方法はEntityListを参照のこと。
	 */
	protected Map<Class<?>, TextureBox> defaultTextures = new HashMap<>();

	@Deprecated
	protected Map<IModelEntity, int[]> stackGetTexturePack = new HashMap<IModelEntity, int[]>();
	@Deprecated
	protected Map<IModelEntity, Object[]> stackSetTexturePack = new HashMap<IModelEntity, Object[]>();

	@Deprecated
	protected List<String[]> searchPrefix = new ArrayList<String[]>();

	@Deprecated
	public static final String[] searchFileNamePrefix = new String[]{"littleMaidMob","mmmlibx","ModelMulti","LittleMaidMob"};

	@Deprecated
	public void init() {
		addSearch("littleMaidMob", "/assets/minecraft/textures/entity/ModelMulti/", "ModelMulti_");
		addSearch("littleMaidMob", "/assets/minecraft/textures/entity/littleMaid/", "ModelMulti_");
		addSearch("littleMaidMob", "/assets/minecraft/textures/entity/littleMaid/", "ModelLittleMaid_");
		addSearch("littleMaidMob", "/mob/ModelMulti/", "ModelMulti_");
		addSearch("littleMaidMob", "/mob/littleMaid/", "ModelLittleMaid_");
	}

	@Deprecated
	protected String[] getSearch(String pName) {
		for (String[] lss : searchPrefix) {
			if (lss[0].equals(pName)) {
				return lss;
			}
		}
		return null;
	}

	/**
	 * 追加対象となる検索対象ファイル群とそれぞれの検索文字列を設定する。
	 */
	@Deprecated
	public void addSearch(String pName, String pTextureDir, String pClassPrefix) {
		searchPrefix.add(new String[] {pName, pTextureDir, pClassPrefix});
	}

	/**
	 * テクスチャ名称の一致する物を返す。
	 */
	public TextureBox getTextureBox(String pName) {
		for (TextureBox ltb : getTextureList()) {
			if (ltb.textureName.equals(pName)) {
				return ltb;
			}
		}
		return null;
	}

	public static List<TextureBox> getTextureList()
	{
		return instance.textures;
	}

	/**
	 * 渡されたTextureBoxBaseを判定してTextureBoxを返す。
	 * @param pBoxBase
	 * @return
	 */
	@Deprecated
	public TextureBox getTextureBox(TextureBoxBase pBoxBase) {
		if (pBoxBase instanceof TextureBox) {
			return (TextureBox)pBoxBase;
		} else if (pBoxBase instanceof TextureBoxServer) {
			return getTextureBox(pBoxBase.textureName);
		}
		return null;
	}

	@Deprecated
	public TextureBoxServer getTextureBoxServer(String pName) {
		for (TextureBoxServer lbox : textureServer) {
			if (lbox.textureName.equals(pName)) {
				return lbox;
			}
		}
		return null;
	}

	@Deprecated
	public TextureBoxServer getTextureBoxServer(int pIndex) {
//		LittleMaidReengaged.Debug("getTextureBoxServer: %d / %d", pIndex, textureServer.size());
		if (textureServer.size() > pIndex) {
			return textureServer.get(pIndex);
		}
		return null;
	}

	@Deprecated
	protected void getArmorPrefix() {
		//1.8検討
		armorFilenamePrefix = new String[]{"leather","chainmail","iron","diamond","gold"};
	}

	@Deprecated
	public boolean loadTextures() {
		LittleMaidReengaged.Debug("loadTexturePacks.");
		// アーマーのファイル名を識別するための文字列を獲得する
		if (CommonHelper.isClient) {
			getArmorPrefix();
		}

		for (String[] lst : searchPrefix) {
			// mods
			searchFiles(FileList.dirMods, lst);
			
			// 開発専用処理
			if (FileList.developIncludeDirMods != null) {
				searchFiles(FileList.developIncludeDirMods, lst);
			}

			for (File classpathDir :
					FileList.dirClasspath) {
				addTexturesDir(classpathDir, classpathDir, lst);
			}
		}

		// TODO 実験コード
		buildCrafterTexture();

		// テクスチャパッケージにモデルクラスを紐付け
		ModelMultiBase[] ldm = modelMap.get(defaultModelName);
		if (ldm == null && !modelMap.isEmpty()) {
			ldm = (ModelMultiBase[])modelMap.values().toArray()[0];
		}
		for (TextureBox ltb : textures) {
			if (ltb.modelName.isEmpty()) {
				ltb.setModels(defaultModelName, null, ldm);
			} else {
				//大文字小文字の差は無視する
				for (String key : modelMap.keySet()) {
					if (key.toLowerCase().equals(ltb.modelName.toLowerCase())) {
						ltb.setModels(ltb.modelName, modelMap.get(key), ldm);
						break;
					}
				}
			}
		}
		for (Entry<String, ModelMultiBase[]> le : modelMap.entrySet()) {
			String ls = le.getValue()[0].getUsingTexture();
			if (ls != null) {
				if (getTextureBox(ls + "_" + le.getKey()) == null) {
					TextureBox lbox = null;
					for (TextureBox ltb : textures) {
						if (ltb.packegeName.equals(ls)) {
							lbox = ltb;
							break;
						}
					}
					if (lbox != null) {
						lbox = lbox.duplicate();
						lbox.setModels(le.getKey(), null, le.getValue());
						textures.add(lbox);
					}
				}
			}
		}
		LittleMaidReengaged.Debug("Loaded Texture Lists.(%d)", textures.size());
		for (TextureBox lbox : textures) {
			LittleMaidReengaged.Debug("texture: %s(%s) - hasModel:%b", lbox.textureName, lbox.fileName, lbox.models != null);
		}
		for (int li = textures.size() - 1; li >= 0; li--) {
			if (textures.get(li).models == null) {
				textures.remove(li);
			}
		}
		LittleMaidReengaged.Debug("Rebuild Texture Lists.(%d)", textures.size());
		for (TextureBox lbox : textures) {
			if(lbox.getWildColorBits()>0){
				setDefaultTexture(EntityLivingBase.class, lbox);
			}
			LittleMaidReengaged.Debug("texture: %s(%s) - hasModel:%b", lbox.textureName, lbox.fileName, lbox.models != null);
		}

		setDefaultTexture(EntityLivingBase.class, getTextureBox("default_" + defaultModelName));

		return false;
	}

	@Deprecated
	private void searchFiles(File ln, String[] lst) {
		LittleMaidReengaged.Debug("getTexture[%s:%s].", lst[0], lst[1]);
		// mods
		for (File lf : ln.listFiles()) {
			boolean lflag;
			if (lf.isDirectory()) {
				// ディレクトリ
				lflag = addTexturesDir(lf, lf, lst);
			} else {
				// zip
				lflag = addTexturesZip(lf, lst);
			}
			LittleMaidReengaged.Debug("getTexture-append-%s-%s.", lf.getName(), lflag ? "done" : "fail");
		}
	}

	public void buildCrafterTexture() {
		// TODO:実験コード標準モデルテクスチャで構築
		TextureBox lbox = new TextureBox("Crafter_Steve", new String[] {"", "", ""});
		lbox.fileName = "";

		lbox.addTexture(0x0c, "/assets/minecraft/textures/entity/lmsteve/steve.png");
		if (armorFilenamePrefix != null && armorFilenamePrefix.length > 0) {
			for (String ls : armorFilenamePrefix) {
				Map<Integer, ResourceLocation> lmap = new HashMap<Integer, ResourceLocation>();
				lmap.put(tx_armor1, new ResourceLocation(
						(new StringBuilder()).append("textures/models/armor/").append(ls).append("_layer_2.png").toString()));
				lmap.put(tx_armor2, new ResourceLocation(
						(new StringBuilder()).append("textures/models/armor/").append(ls).append("_layer_1.png").toString()));
				lbox.armors.put(ls, lmap);
			}
		}

		textures.add(lbox);
	}


	@Deprecated
	public boolean loadTextureServer() {
		// サーバー用テクスチャ名称のインデクッスローダー
		// 先ずは手持ちのテクスチャパックを追加する。
		textureServer.clear();
		for (TextureBox lbox : getTextureList()) {
			textureServer.add(new TextureBoxServer(lbox));
		}
		// ファイルからロード
/*
		File lfile = FMLCommonHandler.instance().getMinecraftServerInstance().getFile(nameTextureIndex);
		if (lfile.exists() && lfile.isFile()) {
			try {
				FileReader fr = new FileReader(lfile);
				BufferedReader br = new BufferedReader(fr);
				String ls;

				while ((ls = br.readLine()) != null) {
					String lt[] = ls.split(",");
					if (lt.length >= 7) {
						// ファイルのほうが優先
						MMM_TextureBoxServer lbox = getTextureBoxServer(lt[6]);
						if (lbox == null) {
							lbox = new MMM_TextureBoxServer();
							textureServer.add(lbox);
						}
						lbox.contractColor	= MMM_Helper.getHexToInt(lt[0]);
						lbox.wildColor		= MMM_Helper.getHexToInt(lt[1]);
						lbox.setModelSize(
								Float.valueOf(lt[2]),
								Float.valueOf(lt[3]),
								Float.valueOf(lt[4]),
								Float.valueOf(lt[5]));
						lbox.textureName	= lt[6];
					}
				}

				br.close();
				fr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			LittleMaidReengaged.Debug("Loaded ServerBoxList.(%d)", textureServer.size());
			for (int li = 0; li < textureServer.size(); li++) {
				MMM_TextureBoxServer lbox = textureServer.get(li);
				LittleMaidReengaged.Debug("%04d=%s:%04x:%04x", li, lbox.textureName, lbox.contractColor, lbox.wildColor);
			}
			return true;
		}
*/
		return false;
	}

	@Deprecated
	public void saveTextureServer() {
		// サーバー用テクスチャ名称のインデクッスセーバー
		File lfile = FMLCommonHandler.instance().getMinecraftServerInstance().getFile(nameTextureIndex);
		try {
			FileWriter fw = new FileWriter(lfile);
			BufferedWriter bw = new BufferedWriter(fw);

			for (TextureBoxServer lbox : textureServer) {
				bw.write(String.format(
						"%04x,%04x,%f,%f,%f,%f,%s",
						lbox.getContractColorBits(),
						lbox.getWildColorBits(),
						lbox.getHeight(null),
						lbox.getWidth(null),
						lbox.getYOffset(null),
						lbox.getMountedYOffset(null),
						lbox.textureName));
				bw.newLine();
			}

			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * テクスチャインデックスを構築。
	 */
	@Deprecated
	public void initTextureList(boolean pFlag) {
		LittleMaidReengaged.Debug("Clear TextureBoxServer.");
		textureServerIndex.clear();
		textureServer.clear();
		if (pFlag) {
			int li = 0;
			for (TextureBox lbc : getTextureList()) {
				TextureBoxServer lbs = new TextureBoxServer(lbc);
				textureServer.add(lbs);
				textureServerIndex.put(lbc, li++);
			}
			LittleMaidReengaged.Debug("Rebuild TextureBoxServer(%d).", textureServer.size());
		}
	}

	/**
	 * 渡された名称を解析してLMM用のモデルクラスかどうかを判定する。
	 * 「ModelLittleMaid_」という文字列が含まれていて、
	 * 「MMM_ModelBiped」を継承していればマルチモデルとしてクラスを登録する。
	 * @param fname
	 */
	@Deprecated
	@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
	protected void addModelClass(String fname, String[] pSearch) {
		// モデルを追加
		int lfindprefix = fname.indexOf(pSearch[2]);
		if (lfindprefix > -1/* && fname.endsWith(".class")*/) {
			String cn = fname.endsWith(".class") ? fname.substring(0,fname.lastIndexOf(".class")) : fname;
			String pn = cn.substring(pSearch[2].length() + lfindprefix);

			if (modelMap.containsKey(pn)) return;
			try {
				Package lpackage = LittleMaidReengaged.class.getPackage();
				Class lclass;
				if (lpackage != null) {
//					cn = (new StringBuilder("")).append(".").append(cn).toString();
					cn = cn.replace("/", ".");
					System.out.println("MMM_TextureManager.addModelClass : "+cn);
					lclass = LittleMaidReengaged.class.getClassLoader().loadClass(cn);
				} else {
					lclass = Class.forName(cn);
				}
				if (!(ModelMultiBase.class).isAssignableFrom(lclass) || Modifier.isAbstract(lclass.getModifiers())) {
					LittleMaidReengaged.Debug("getModelClass-fail.");
					return;
				}
				ModelMultiBase mlm[] = new ModelMultiBase[3];
				Constructor<ModelMultiBase> cm = lclass.getConstructor(float.class);
				mlm[0] = cm.newInstance(0.0F);
				float[] lsize = mlm[0].getArmorModelsSize();
				mlm[1] = cm.newInstance(lsize[0]);
				mlm[2] = cm.newInstance(lsize[1]);
				modelMap.put(pn, mlm);
				LittleMaidReengaged.Debug("getModelClass-%s:%s", pn, cn);
			}
			catch (Exception exception) {
				LittleMaidReengaged.Debug("getModelClass-Exception: %s", fname);
				if(DevMode.DEVELOPMENT_DEBUG_MODE || LMRConfig.cfg_PrintDebugMessage) exception.printStackTrace();
			}
			catch (Error error) {
				LittleMaidReengaged.Debug("getModelClass-Error: %s", fname);
				if(DevMode.DEVELOPMENT_DEBUG_MODE || LMRConfig.cfg_PrintDebugMessage) error.printStackTrace();
			}
		}
	}

	@Deprecated
	protected boolean addTextureName(String fname, String[] pSearch) {
		// パッケージにテクスチャを登録
		if (!fname.startsWith("/")) {
			fname = (new StringBuilder()).append("/").append(fname).toString();
		}

//		LittleMaidReengaged.Debug("MMM_TextureManager.addTextureName : %s # %s # %s # %s", fname, pSearch[0], pSearch[1], pSearch[2]);
		if (fname.toLowerCase().startsWith(pSearch[1].toLowerCase())) {
			int i = fname.lastIndexOf("/");
			if (pSearch[1].length() < i) {
				String pn = fname.substring(pSearch[1].length(), i);
				pn = pn.replace('/', '.');
				String fn = fname.substring(i);
				int lindex = getIndex(fn);
				if (lindex > -1) {
					if (lindex == tx_oldarmor1) {
						lindex = tx_armor1;
					}
					if (lindex == tx_oldarmor2) {
						lindex = tx_armor2;
					}
					if (lindex == tx_oldwild) {
						lindex = tx_wild + 12;
					}
					TextureBox lts = getTextureBox(pn);
					if (lts == null) {
						lts = new TextureBox(pn, pSearch);
						textures.add(lts);
						LittleMaidReengaged.Debug("getTextureName-append-texturePack-%s", pn);
					}
					//lts.addTexture(lindex, fname);
					lts.addTexture(lindex, fname);
					return true;
				}
			}
		}
		return false;
	}

	@Deprecated
	protected boolean addTexturesZip(File file, String[] pSearch) {
		//
		if (file == null || file.isDirectory()) {
			return false;
		}
		try {
			FileInputStream fileinputstream = new FileInputStream(file);
			ZipInputStream zipinputstream = new ZipInputStream(fileinputstream);
			ZipEntry zipentry;
			LittleMaidReengaged.Debug("Start searching %s", file.getName());
			do {
				zipentry = zipinputstream.getNextEntry();
				if(zipentry == null)
				{
					break;
				}
				if (!zipentry.isDirectory()) {
					if (zipentry.getName().endsWith(".class")) {
						addModelClass(zipentry.getName(), pSearch);
					} else if(zipentry.getName().endsWith(".png")){
						String lt1 = "mob/littleMaid";
						String lt2 = "mob/ModelMulti";
						addTextureName(zipentry.getName(), pSearch);
						if(FMLCommonHandler.instance().getSide()==Side.CLIENT &&
								(zipentry.getName().startsWith(lt1) 
										|| zipentry.getName().startsWith(lt2)
										|| (!zipentry.getName().equals(zipentry.getName().toLowerCase())))) {
							OldZipTexturesWrapper.addTexturePath(zipentry.getName());
						}
					}
				}
			} while(true);

			zipinputstream.close();
			fileinputstream.close();

			return true;
		} catch (Exception exception) {
			LittleMaidReengaged.Debug("addTextureZip-Exception.");
			return false;
		}
	}

	@Deprecated
	protected boolean addTexturesDir(File file, File root, String[] pSearch) {
		// modsフォルダに突っ込んであるものも検索、再帰で。
		if (file == null) {
			return false;
		}

		try {
			for (File nfile : file.listFiles()) {
				if(nfile.isDirectory()) {
					addTexturesDir(nfile, root, pSearch);
				} else {
					String tn = FileClassUtil.getLinuxAntiDotName(nfile.getAbsolutePath());
					String rmn = FileClassUtil.getLinuxAntiDotName(root.getAbsolutePath());
					if (nfile.getName().endsWith(".class")) {
						if(tn.startsWith(rmn)) addModelClass(FileClassUtil.getClassName(tn, rmn), pSearch);
					} else if(nfile.getName().endsWith(".png")) {
						String s = nfile.getPath().replace('\\', '/');

						int i = s.toLowerCase().indexOf(pSearch[1].toLowerCase());
						if (i > -1) {
							// 対象はテクスチャディレクトリ
							addTextureName(s.substring(i), pSearch);
							String lt1 = "mob/littleMaid";
							String lt2 = "mob/ModelMulti";
							String loc = s.substring(i);
							if (loc.startsWith("/")) {
								loc = loc.substring(1);
							}
							if(FMLCommonHandler.instance().getSide()==Side.CLIENT &&
									(loc.startsWith(lt1) 
											|| loc.startsWith(lt2)
											|| (!loc.equals(loc.toLowerCase())))) {
								OldZipTexturesWrapper.addTexturePath(loc);
							}
//							addTextureName(s.substring(i).replace('\\', '/'));
						}
					}/* else {
						// サブフォルダ分のアーカイブを検索
						addTexturesZip(nfile, pSearch);
					}*/
				}
			}
			return true;
		} catch (Exception e) {
			LittleMaidReengaged.Debug("addTextureDebug-Exception.");
			return false;
		}
	}

	@Deprecated
	protected int getIndex(String name) {
		// 名前からインデックスを取り出す
		for (int i = 0; i < defNames.length; i++) {
			if (name.endsWith(defNames[i])) {
				return i;
			}
		}

		Pattern p = Pattern.compile("_([0-9a-f]+).png");
		Matcher m = p.matcher(name);
		if (m.find()) {
			return Integer.decode("0x" + m.group(1));
		}

		return -1;
	}

	public TextureBox getNextPackege(TextureBox pNowBox, int pColor) {
		// 次のテクスチャパッケージの名前を返す
		boolean f = false;
		TextureBox lreturn = null;
		for (TextureBox ltb : getTextureList()) {
			if (ltb.hasColor(pColor)) {
				if (f) {
					return ltb;
				}
				if (lreturn == null) {
					lreturn = ltb;
				}
			}
			if (ltb == pNowBox) {
				f = true;
			}
		}
		return lreturn == null ? null : lreturn;
	}

	public TextureBox getPrevPackege(TextureBox pNowBox, int pColor) {
		// 前のテクスチャパッケージの名前を返す
		TextureBox lreturn = null;
		for (TextureBox ltb : getTextureList()) {
			if (ltb == pNowBox) {
				if (lreturn != null) {
					break;
				}
			}
			if (ltb.hasColor(pColor)) {
				lreturn = ltb;
			}
		}
		return lreturn == null ? null : lreturn;
	}

	/**
	 * ローカルで読み込まれているテクスチャパックの数。
	 */
	public int getTextureCount() {
		return getTextureList().size();
	}

	public TextureBox getNextArmorPackege(TextureBox pNowBox) {
		// 次のテクスチャパッケージの名前を返す
		boolean f = false;
		TextureBox lreturn = null;
		for (TextureBox ltb : getTextureList()) {
			if (ltb.hasArmor()) {
				if (f) {
					return ltb;
				}
				if (lreturn == null) {
					lreturn = ltb;
				}
			}
			if (ltb == pNowBox) {
				f = true;
			}
		}
		return lreturn;
	}

	public TextureBox getPrevArmorPackege(TextureBox pNowBox) {
		// 前のテクスチャパッケージの名前を返す
		TextureBox lreturn = null;
		for (TextureBox ltb : getTextureList()) {
			if (ltb == pNowBox) {
				if (lreturn != null) {
					break;
				}
			}
			if (ltb.hasArmor()) {
				lreturn = ltb;
			}
		}
		return lreturn;
	}

	/**
	 * スポーン用モデル名をランダムに取得する
	 */
	public String getRandomTextureString(Random pRand) {
		//return getRandomTexture(pRand).textureName;
		
		// 野生色があるものをリストアップ
		List<TextureBox> llist = new ArrayList<>();
		for (TextureBox lbox : this.textures) {
			if (lbox.getWildColorBits() > 0) {
				llist.add(lbox);
			}
		}
		TextureBox wild = llist.get(pRand.nextInt(llist.size()));
		
		return wild.textureName;
	}

	@Deprecated
	public TextureBoxServer getRandomTexture(Random pRand) {
		if (textureServer.isEmpty()) {
			return null;
		}
		// 野生色があるものをリストアップ
		List<TextureBoxServer> llist = new ArrayList<TextureBoxServer>();
		for (TextureBoxServer lbox : textureServer) {
			if (lbox.getWildColorBits() > 0) {
				llist.add(lbox);
			}
		}
		return llist.get(pRand.nextInt(llist.size()));
	}

	/**
	 * テクスチャパック名に対応するインデックスを返す。
	 * 基本サーバー用。
	 * @param pEntity
	 * @param pPackName
	 * @return
	 */
	@Deprecated
	public int getIndexTextureBoxServer(IModelEntity pEntity, String pPackName) {
		for (int li = 0; li < textureServer.size(); li++) {
			if (textureServer.get(li).textureName.equals(pPackName)) {
				return li;
			}
		}
		// 見当たらなかったのでEntityに対応するデフォルトを返す
//		int li = textureServerIndex.get(getDefaultTexture(pEntity));
		TextureBox lbox = getDefaultTexture(pEntity);
		if (lbox != null) {
			pPackName = lbox.textureName;
			for (int li = 0; li < textureServer.size(); li++) {
				if (textureServer.get(li).textureName.equals(pPackName)) {
					return li;
				}
			}
		}
		return 0;
	}

	/**
	 * 指定されたテクスチャパックのサーバー側の管理番号を返す。
	 * @param pBox
	 * @return
	 */
	@Deprecated
	public int getIndexTextureBoxServerIndex(TextureBox pBox) {
		return textureServerIndex.get(pBox);
	}

	/**
	 * Entityに対応するデフォルトのテクスチャを設定する。
	 */
	@Deprecated
	public void setDefaultTexture(IModelEntity pEntity, TextureBox pBox) {
		setDefaultTexture(pEntity.getClass(), pBox);
	}
	
	public void setDefaultTexture(Class<?> pEntityClass, TextureBox pBox) {
		defaultTextures.put(pEntityClass, pBox);
		LittleMaidReengaged.Debug("appendDefaultTexture:%s(%s)",
				pEntityClass.getSimpleName(), pBox == null ? "NULL" : pBox.textureName);
	}

	/**
	 * Entityに対応するデフォルトモデルを返す。
	 */
	public TextureBox getDefaultTexture(IModelEntity pEntity) {
		return getDefaultTexture(pEntity.getClass());
	}
	
	public TextureBox getDefaultTexture(Class<?> pEntityClass) {
		if (defaultTextures.containsKey(pEntityClass)) {
			return defaultTextures.get(pEntityClass);
		}
		Class<?> lsuper = pEntityClass.getSuperclass();
		if (lsuper != null) {
			TextureBox lbox = getDefaultTexture(lsuper);
			if (lbox != null) {
				setDefaultTexture(pEntityClass, lbox);
			}
			return lbox;
		}
		return null;
	}
	
	/**
	 * LMFileLoaderで読み込んだ情報をもとにメイドさんモデルをセットアップする
	 */
	public void createLittleMaidModels() {
		
		LittleMaidReengaged.logger.info("createLittleMaidModels : start");
		
		//マルチモデル生成
		for (String key : LMMultiModelHandler.multiModelClassMap.keySet()) {
			
			Class<? extends ModelMultiBase> clazz;
			
			try {
				
				//マルチモデルクラスをインスタンス化
				clazz = LMMultiModelHandler.multiModelClassMap.get(key);
				
				ModelMultiBase mmBase[] = new ModelMultiBase[3];
			
				Constructor<? extends ModelMultiBase> constructorMMBase = clazz.getConstructor(float.class);
				mmBase[0] = constructorMMBase.newInstance(0.0F);
				float[] lsize = mmBase[0].getArmorModelsSize();
				mmBase[1] = constructorMMBase.newInstance(lsize[0]);
				mmBase[2] = constructorMMBase.newInstance(lsize[1]);

				modelMap.put(key, mmBase);
				
				//モデルロードメッセージ
				LittleMaidReengaged.logger.info(String.format("ModelManager-Load-MultiModel : %s", key));

			} catch (Exception e) {
				LittleMaidReengaged.logger.error(String.format("ModelManager-MultiModelInstanceException : %s", ""));
				if (LMRConfig.cfg_PrintDebugMessage) e.printStackTrace();
			}
			
		}
		
		//テクスチャを生成
		for (String key : LMTextureHandler.textureMap.keySet()) {
			
			//TextureBox生成
			TextureBox textureBox = new TextureBox(key, new String[]{"", "", ""});
			textures.add(textureBox);
			
			//TextureBoxにテクスチャを登録
			for (String texturePath : LMTextureHandler.textureMap.get(key)) {
				//bind用texture
				String bindTexturePath = texturePath.replace("assets/minecraft/", "");
				int colorIndex = LMTextureHandler.getColorIndex(texturePath);
				
				//colorごとに追加
				textureBox.addTexture(colorIndex, bindTexturePath);
				
				//旧テクスチャパス対応
				//OldZipTexturesWrapperで差し替えて対応する
				if(FMLCommonHandler.instance().getSide() == Side.CLIENT &&
						((!texturePath.equals(texturePath.toLowerCase())))) {
					OldZipTexturesWrapper.addTexturePath(texturePath);
				}	
			}
			
			//モデルロードメッセージ
			LittleMaidReengaged.logger.info(String.format("ModelManager-Load-Texture : %s", key));

		}
		
		//スティーブモデルの登録
		buildCrafterTexture();
		
		//TextureBoxとMultiModelの紐づけ
		//デフォルトモデル設定
		ModelMultiBase[] defaultModel = modelMap.get(defaultModelName);
		if (defaultModel == null && !modelMap.isEmpty()) {
			defaultModel = (ModelMultiBase[])modelMap.values().toArray()[0];
		}
		
		//TextureBoxベースでメイドモデルを紐づける
		for (TextureBox textureBox : textures) {
			if (!textureBox.modelName.isEmpty()) {
				for (String key : modelMap.keySet()) {
					//モデル名に一致するモデルをTextureBoxへ設定する
					if (key.toLowerCase().equals(textureBox.modelName.toLowerCase())) {
						textureBox.setModels(textureBox.modelName, modelMap.get(key), defaultModel);
						break;
					}
				}				
			} else {
				//モデル名の設定がない場合は標準モデルを設定する
				textureBox.setModels(defaultModelName, null, defaultModel);
			}
		}
		
		//マルチモデル側からテクスチャを設定する場合に利用する
		//基本的に使用されていない
		for (Entry<String, ModelMultiBase[]> entryModelMap : modelMap.entrySet()) {
			String modelTexture = entryModelMap.getValue()[0].getUsingTexture();
			if (modelTexture != null) {
				if (getTextureBox(modelTexture + "_" + entryModelMap.getKey()) == null) {
					TextureBox textureBox = null;
					for (TextureBox ltb : textures) {
						if (ltb.packegeName.equals(modelTexture)) {
							textureBox = ltb;
							break;
						}
					}
					if (textureBox != null) {
						textureBox = textureBox.duplicate();
						textureBox.setModels(entryModelMap.getKey(), null, entryModelMap.getValue());
						textures.add(textureBox);
					}
				}
			}
		}

		//TextureBoxのモデルがnullのものを削除する
		Iterator<TextureBox> iteratorTextures = textures.iterator();
		while(iteratorTextures.hasNext()){
			TextureBox textureBox = iteratorTextures.next();
			if (textureBox.models == null) {
				iteratorTextures.remove();
			}
		}
		
		//野生持ちTextureBoxをdefaultModelに設定する
		for (TextureBox lbox : textures) {
			if(lbox.getWildColorBits()>0){
				setDefaultTexture(EntityLivingBase.class, lbox);
			}
		}

		//defaultモデルの設定
		setDefaultTexture(EntityLivingBase.class, getTextureBox("default_" + defaultModelName));
		
		LittleMaidReengaged.logger.info("createLittleMaidModels : end");

	}

}
