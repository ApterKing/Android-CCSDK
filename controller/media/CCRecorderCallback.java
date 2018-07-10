package cc.sdkutil.controller.media;

import cc.sdkutil.model.media.CCRecorderError;

/**
 * Created by wangcong on 15-4-21.
 * 用于监听CCRecorder
 */
public class CCRecorderCallback {

    /**
     * 录音错误
     * @param error
     * @param e
     */
    public void onError(CCRecorderError error, Exception e) {

    }

    /**
     * 录音时间改变，单位毫秒
     * @param recordTime millisecond
     */
    public void onSeekRecord(int recordTime) {

    }

    /**
     * 录音开始
     */
    public void onStart() {

    }

    /**
     * 录音结束
     * @param recordPath
     * @param isTimeTerminal 是否是到达最大录音时间
     */
    public void onStop(String recordPath, boolean isTimeTerminal) {

    }

}
