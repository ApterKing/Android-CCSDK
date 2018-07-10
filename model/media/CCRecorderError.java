package cc.sdkutil.model.media;

/**
 * Created by wangcong on 15-4-21.
 */
public enum CCRecorderError {

    ERROR_MINDURATION("录音时长小于最短时间限制"),
    ERROR_INTERNAL("MediaRecorder初始化音频错误"),
    ERROR_IOEXCEPTION("播放文件打开失败 IOException");

    String values;

    CCRecorderError(String error) {
        this.values = error;
    }

    @Override
    public String toString() {
        return values;
    }

}
