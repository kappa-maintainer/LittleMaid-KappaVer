package net.blacklab.lmr.entity.littlemaid;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import net.blacklab.lmr.LittleMaidReengaged;
import net.blacklab.lmr.config.LMRConfig;
import net.blacklab.lmr.network.LMRMessage;
import net.blacklab.lmr.util.EnumSound;
import net.blacklab.lmr.util.manager.SoundManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

/**
 * メイドさんの音声制御用
 * @author firis-games
 * 
 * 1.playVoiceSoundで音声リストへ登録する
 * 2.onEntityUpdateで音声リストに音声が存在する場合に音声再生する
 */
public class LittleMaidSoundManager {
	
	private final EntityLittleMaid maid;
	
	private int maidVoiceSoundInterval = 0;
	
	private static int VOICE_INTERVAL = 10;
	
	//メイドさんのダメージを受けた際のSound設定
	private EnumSound lmDamageSound;
	
	//音声再生リスト
	private CopyOnWriteArrayList<EnumSound> playingSound = new CopyOnWriteArrayList<EnumSound>();
	
	/**
	 * ダメージ音声を設定する
	 */
	public void setDamageSound(EnumSound sound) {
		this.lmDamageSound = sound;
	}
	
	/**
	 * ダメージ音声を取得する
	 */
	public EnumSound getDamageSound() {
		return this.lmDamageSound;
	}
	
	/**
	 * コンストラクタ
	 * @param maid
	 */
	public LittleMaidSoundManager(EntityLittleMaid maid) {
		this.maid = maid;
		this.maidVoiceSoundInterval = 0;
		this.lmDamageSound = EnumSound.hurt;
	}
	
	/**
	 * メイドさんのダメージ音声再生用
	 * @param sound
	 * @param isRandom
	 */
	public void playDamageVoiceSound(boolean isRandom) {
		this.playVoiceSound(this.lmDamageSound, isRandom);
	}
	
	/**
	 * メイドさんの音声再生処理
	 * サーバーからクライアントへパケットを投げるだけ
	 * パケット数を減らすためにランダム判定を
	 */
	public void playVoiceSound(EnumSound sound, boolean isRandom) {
		
		//Null設定の場合はなにもしない
		if (EnumSound.Null == sound) return;
		
		//ランダムの場合はここでレートの設定を行う
		if (isRandom && !isRandomPlayVoiceSound()) {
			//再生対象外
			return;
		}
		
		if (!maid.world.isRemote) {
			//サーバーサイド
			LittleMaidReengaged.Debug("id:%d-%s, seps:%04x-%s", maid.getEntityId(), "Server",  sound.index, sound.name());
			
			//送信用パケット生成
			NBTTagCompound tagCompound = new NBTTagCompound();
			tagCompound.setInteger("Sound", sound.index);
			
			//パケット送信
			maid.syncNet(LMRMessage.EnumPacketMode.CLIENT_PLAY_SOUND, tagCompound);
			
		} else {
			//クライアントサイド
			if (this.maidVoiceSoundInterval <= 0) {
				
				//再生用リストへ追加
				playingSound.add(sound);
				
				//インターバル追加
				this.maidVoiceSoundInterval = VOICE_INTERVAL;
			}
		}
	}
	
	
	/**
	 * レート設定をもとにボイス再生をするかの判断を行う
	 * @return
	 */
	protected boolean isRandomPlayVoiceSound() {
		if(Math.random() > LMRConfig.cfg_voiceRate) {
			return false;
		}
		return true;
	}
	
	/**
	 * 音声の再生はonEntityUpdate内で行っている
	 */
	public void onEntityUpdate() {
		
		//クライアントサイドかつサウンドリストが存在する場合のみ処理を行う
		if (!maid.world.isRemote || playingSound.isEmpty()) return;
		
		String textureName = maid.textureNameMain;
		int textureColor = maid.getColor();
		
		//サウンド設定されている分まとめて再生する
		Iterator<EnumSound> iterator = playingSound.iterator();
		while(iterator.hasNext()){
			
			EnumSound sound = iterator.next();
			LittleMaidReengaged.Debug("REQ %s", sound);

			//音声パックがロードされていない場合は通常音声として再生する
			if (!SoundManager.instance.isFoundSoundpack()) {
				this.maid.playSound(sound.DefaultValue, 1.0f);
				playingSound.remove(sound);
				continue;
			}

			//ボイスパックから再生する
			String soundName = SoundManager.instance.getSoundNameWithModel(sound, textureName, textureColor);
			LittleMaidReengaged.Debug("STC %s,%d/FRS %s", textureName, textureColor, soundName);
			
			//対象が存在しない場合は次へ
			if (soundName == null || soundName.isEmpty()) {
				playingSound.remove(sound);
				continue;
			}
			
			//音声レートを取得する（作り直し予定）
			//音声の個別レートを判断する
			/*
			if ((sound.index & 0xf00) == EnumSound.living_daytime.index) {
				// LivingSound LivingVoiceRateを確認
				Float ratio = SoundRegistry.getLivingVoiceRatio(soundName);
				if (ratio == null) ratio = LMRConfig.cfg_voiceRate;
				// カットオフ
				if (maid.rand.nextFloat() > ratio) {
					playingSound.remove(sound);
					continue;
				}
			}
			*/
			
			//音声の再生
			LittleMaidReengaged.Debug(String.format("id:%d, se:%04x-%s (%s)", maid.getEntityId(), sound.index, sound.name(), soundName));

			SoundEvent soundEvent = new SoundEvent(new ResourceLocation(LittleMaidReengaged.DOMAIN, soundName));
			this.maid.world.playSound(maid.posX, maid.posY, maid.posZ, 
					soundEvent, maid.getSoundCategory(), maid.getSoundVolume(), 1.0F, false);
			playingSound.remove(sound);
			
		}

	}
	
	public void onUpdate() {
		if (maidVoiceSoundInterval > 0) {
			maidVoiceSoundInterval--;
		}
	}
	
}
