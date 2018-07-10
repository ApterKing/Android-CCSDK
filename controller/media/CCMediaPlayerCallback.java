package cc.sdkutil.controller.media;

import cc.sdkutil.model.media.CCMediaPlayerError;

/**
 * Created by wangcong on 15-4-21.
 * 用于CCMediaPlayer 监听
 */
public class CCMediaPlayerCallback {

    /**
     * 开始播放
     */
    public void onStart() {

    }

    /**
     * 播放发生错误
     * @param error
     * @param e
     */
    public void onError(CCMediaPlayerError error, Exception e) {

    }

    /**
     * 播放进度
     * @param currentPosition   当前播放时间
     * @param duration          音频总时间
     */
    public void onSeekComplete(int currentPosition, int duration) {

    }

    /**
     * 停止播放
     * @param isComplete 是否播放完成
     */
    public void onStop(boolean isComplete) {

    }
}
