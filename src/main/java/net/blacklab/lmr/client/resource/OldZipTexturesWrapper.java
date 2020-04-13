package net.blacklab.lmr.client.resource;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.blacklab.lmr.LittleMaidReengaged;
import net.blacklab.lmr.config.LMRConfig;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

/**
 * 旧方式テクスチャのロード用リソースパック
 * @author firis-games
 *
 */
public class OldZipTexturesWrapper implements IResourcePack {

	protected static ArrayList<String> keys = new ArrayList<String>();

	@Override
	public InputStream getInputStream(ResourceLocation arg0) throws IOException {
		if(resourceExists(arg0)){
			String key = texturesResourcePath(arg0);
			key = containsKey(key);
			InputStream stream = getInputStreamFromResoucepacks(key);
			if (stream == null) {
				stream = LittleMaidReengaged.class.getClassLoader().getResourceAsStream(key);
			}
			return stream;
		}
		return null;
	}
	
	@Override
	public BufferedImage getPackImage() throws IOException {
		return null;
	}

	@Override
	public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer arg0,
			String arg1) throws IOException {
		return null;
	}

	@Override
	public String getPackName() {
		return "OldTexturesLoader";
	}

	@Override
	public Set<String> getResourceDomains() {
		return ImmutableSet.of("minecraft");
	}

	@Override
	public boolean resourceExists(ResourceLocation arg0) {
		
		String key = texturesResourcePath(arg0);
		if (key == null) {
			return false;
		}
		
		return containsKey(key) == null ? false : true;
	}
	
	/**
	 * テクスチャパックのリソースパスへ変換する
	 * @param path
	 * @return
	 */
	public String texturesResourcePath(ResourceLocation path) {
		
		String key = path.getResourcePath();
		
		if (!key.endsWith(".png")) return null;

		if(key.startsWith("/")) key = key.substring(1);
		
		//旧式用の判定処理
		if (key.toLowerCase().startsWith("mob/modelmulti")
				|| key.toLowerCase().startsWith("mob/littlemaid")) {
			//旧方式は何も加工しない
		} else {
			key = "assets/minecraft/" + key;
		}

		return key;
	}
	
	/**
	 * テクスチャリストの中に対象テクスチャが含まれるかチェックする
	 * 大文字小文字は区別しない
	 * @param path
	 * @return
	 */
	public String containsKey(String path) {
		
		String ret = null;
		
		for (String key : keys) {
			if (key.toLowerCase().equals(path.toLowerCase())) {
				ret = key;
				break;
			}
		}
		
		return ret;
		
	}
	
	/**
	 * Textureを追加する
	 * @param texture
	 */
	public static void addTexturePath(String texture) {
		keys.add(texture);
	}
	
	/**
	 * 機能が有効な場合
	 * @return
	 * @throws IOException 
	 */
	protected InputStream getInputStreamFromResoucepacks(String key) throws IOException {
		
		//設定が無効の場合は何もしない
		if (!LMRConfig.cfg_loader_texture_load_from_resoucepack) return null;
		
		//設定系の初期化
		createResourcepacksConfig();
		
		Path path = Paths.get(resourcepacksPath.toString(), key);
		if (Files.exists(path)) {
			return Files.newInputStream(path);
		}
		return null;
	}
	
	private static Path resourcepacksPath = Paths.get("resourcepacks", "LittleMaidResource");
	
	/**
	 * テクスチャの直接読込処理用設定を作成する
	 */
	public static void createResourcepacksConfig() {
		
		//設定が無効の場合は何もしない
		if (!LMRConfig.cfg_loader_texture_load_from_resoucepack) return;
		
		//フォルダを作成する
		Path basePath = resourcepacksPath;
		
		//ベースフォルダが存在しない場合
		if (Files.notExists(basePath)) {
			//必要ファイルを作成する
			try {
				//ベースフォルダ作成
				Files.createDirectories(basePath);
				
				//作業用パス作成
				Path workPath = Paths.get(basePath.toString(), "assets/minecraft/textures/entity");
				Files.createDirectories(workPath);
				
				//リソースパック用設定ファイル出力
				Path packmetaPath = Paths.get(basePath.toString(), "pack.mcmeta");
				String packmeta = "{\"pack\": {\"pack_format\": 3,\"description\": \"LittleMaidReengaged Developer Resourcepack\"}}";
				Files.write(packmetaPath, Arrays.asList(packmeta), Charset.forName("UTF-8"), StandardOpenOption.CREATE);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
