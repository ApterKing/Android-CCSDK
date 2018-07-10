package cc.sdkutil.controller.media;

import android.media.MediaRecorder;
import android.os.Handler;

import java.io.File;
import java.io.IOException;

import cc.sdkutil.controller.util.CCLogUtil;
import cc.sdkutil.model.media.CCRecorderError;

/**
 * Created by wangcong on 15-4-21.
 */
public class CCRecorder {

    private static CCRecorder instance = null;
    private MediaRecorder mRecorder = null;

    private int minDuration;         // 最短录音时间, 小于该时间的音频将不存在
    private int maxDuration;         // 最长录音时间

    private Handler mHandler;
    boolean isStoped;
    private int recordTime;
    // 临时数据
    private CCRecorderCallback mCallBack;
    private String mRecordPath;

    public synchronized static CCRecorder newInstance() {
        if (instance == null) {
            instance = new CCRecorder();
        }
        return instance;
    }

    private CCRecorder() {
        mHandler = new Handler();

        initRecorder();
    }

    private void initRecorder() {
        mRecorder = new MediaRecorder();
        //指定音频来源（麦克风）
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //指定音频输出格式
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        //指定音频编码方式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }

    public void setMinDuration(int minDuration) {
        this.minDuration = minDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    /**
     * 开始录音
     * @param recordPath
     */
    public void start(String recordPath, CCRecorderCallback callback) {
        //指定录制音频输出信息的文件
        this.mCallBack = callback;
        this.mRecordPath = recordPath;
        if (mRecorder == null) initRecorder();
        try {
            CCLogUtil.e("fuck-voice-path", recordPath);
            mRecorder.setOutputFile(recordPath);
            mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    if (mCallBack != null) {
                        mCallBack.onError(CCRecorderError.ERROR_IOEXCEPTION,
                                new RuntimeException(CCRecorderError.ERROR_IOEXCEPTION.toString()
                                        + "--" + what + "---" + extra));
                        isStoped = true;
                    }
                }
            });
            mRecorder.prepare();
            mRecorder.start();
            if (mCallBack != null)
                mCallBack.onStart();
            isStoped = false;
            postTime();
        } catch (IllegalStateException | IOException e) {
            if (mCallBack != null) {
                mCallBack.onError(CCRecorderError.ERROR_IOEXCEPTION, e);
            }
        }
    }

    /**
     * 停止录音
     */
    public void stop() {
        innerStop(false);
    }

    private void innerStop(boolean isTimeTerminal) {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }

        // 小于最小时间
        if (minDuration != 0 && recordTime <= minDuration) {
            if (mCallBack != null) mCallBack.onError(CCRecorderError.ERROR_MINDURATION,
                    new RuntimeException(CCRecorderError.ERROR_MINDURATION.toString()));
            File file = new File(mRecordPath);
            file.deleteOnExit();
            return;
        }

        isStoped = true;
        recordTime = 0;
        if (mCallBack != null)
            mCallBack.onStop(mRecordPath, isTimeTerminal);
    }

    /**
     * 刷新录音时间
     */
    private void postTime() {
        if (isStoped == false) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recordTime += 100;
                    if (maxDuration != 0 && recordTime > maxDuration) {
                        recordTime = maxDuration;
                        innerStop(true);
                    }
                    if (mCallBack != null)
                        mCallBack.onSeekRecord(recordTime);
                    postTime();
                }
            }, 100);
        }
    }

}
