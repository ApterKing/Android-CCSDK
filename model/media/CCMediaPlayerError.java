package cc.sdkutil.model.media;

/**
 * Created by wangcong on 15-4-21.
 */
public enum  CCMediaPlayerError {

    ERROR_INTERNAL("MediaPlayer初始化音频错误"),
    ERROR_IOEXCEPTION("播放文件打开失败 IOException");

    String values;

    CCMediaPlayerError(String error) {
        this.values = error;
    }

    @Override
    public String toString() {
        return values;
    }
}
