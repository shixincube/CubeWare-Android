package com.common.utils.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * 响铃工具类
 *
 * @author PengZhenjin
 * @date 2017-2-24
 */
public class RingtoneUtil {
    protected static SoundPool soundPool;
    private static   int          ring;
    private static   int          id;
    private static AudioManager audioManager;

    /**
     * 播放
     */
    public static void play(int res, Context context) {
        audioManager = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE));
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        if (soundPool == null) {
            soundPool = new SoundPool(1, audioManager.getMode() == AudioManager.STREAM_MUSIC ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_RING, 50);
            //soundPool = new SoundPool(1, audioManager.getMode() == AudioManager.STREAM_MUSIC ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC, 50);
        }
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                id = soundPool.play(ring, 0.5f, 0.5f, 1, -1, 1);
            }
        });
        try{
            //铃声加载异常
            ring = soundPool.load(context, res, 1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 播放一次
     */
    public static void play1(int res, Context context) {
        audioManager = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE));
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        if (soundPool == null) {
            soundPool = new SoundPool(1, audioManager.getMode() == AudioManager.STREAM_MUSIC ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_RING, 50);
            //soundPool = new SoundPool(1, audioManager.getMode() == AudioManager.STREAM_MUSIC ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC, 50);
        }
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                id = soundPool.play(ring, 0.5f, 0.5f, 1, 0, 1);
            }
        });
        ring = soundPool.load(context, res, 1);
    }

    /**
     * 释放
     */
    public static void release() {
        if (soundPool != null && id > 0) {
            soundPool.setOnLoadCompleteListener(null);
            soundPool.stop(id);
            soundPool.release();
            soundPool = null;
        }
        if (audioManager != null) {
            audioManager.abandonAudioFocus(null);
        }
    }
}
