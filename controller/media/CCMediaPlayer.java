package cc.sdkutil.controller.media;

import android.media.MediaPlayer;

import java.io.IOException;

import cc.sdkutil.controller.util.CCLogUtil;
import cc.sdkutil.model.inject.CCDebug;
import cc.sdkutil.model.media.CCMediaPlayerError;

/**
 * Created by wangcong on 15-4-21.
 * 用于播放简单音乐
 */
@CCDebug
public class CCMediaPlayer {

    private static CCMediaPlayer instance = null;

    private MediaPlayer mPlayer;
    private CCMediaPlayerCallback mCallBack;

    public synchronized static CCMediaPlayer newInstance() {
        if (instance == null) {
            instance = new CCMediaPlayer();
        }
        return instance;
    }

    private CCMediaPlayer() {
        mPlayer = new MediaPlayer();
    }

    /**
     * 播放音乐
     * @param path
     */
    public void playVoice(String path, CCMediaPlayerCallback callback) {
        try {
            this.mCallBack = callback;

            if (mPlayer == null) mPlayer = new MediaPlayer();
            mPlayer.reset();
            CCLogUtil.e("play-path", path);
            mPlayer.setDataSource(path);
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    if (mCallBack != null) {
                        mCallBack.onError(CCMediaPlayerError.ERROR_INTERNAL,
                                new RuntimeException(CCMediaPlayerError.ERROR_INTERNAL.toString()
                                        + "---" + what + "---" + extra));
                    }
                    return false;
                }
            });

            mPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    if (mCallBack != null)
                        mCallBack.onSeekComplete(mp.getCurrentPosition(), mp.getDuration());
                }
            });

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    if (mCallBack != null)
                        mCallBack.onStop(true);
                }
            });

            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mPlayer.start();
                    if (mCallBack != null)
                        mCallBack.onStart();
                }
            });

        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
            if (mCallBack != null)
                mCallBack.onError(CCMediaPlayerError.ERROR_IOEXCEPTION, e);
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (mPlayer == null) return;
        mPlayer.reset();
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        if (mCallBack != null) {
            mCallBack.onStop(false);
        }
    }
}
