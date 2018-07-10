package cc.sdkutil.controller.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import cc.sdkutil.model.inject.CCDebug;

/**
 * Created by wangcong on 15-4-28.
 * 用于异步处理消息
 */
@CCDebug
public class CCAsyncHandler extends Handler {

    public final static String TAG = "CCAsyncHandler";

    private Handler mWorkThreadHandler;
    private Looper mWorkLooper;

    /**
     * 内部工作Handler，用于处理异步消息
     */
    private class CCWorkHandler extends Handler {

        private Handler replyHandler;

        public CCWorkHandler(Looper looper, Handler handler) {
            super(looper);
            this.replyHandler = handler;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 处理异步消息
            onAsyncDeal(msg);

            // 处理完成后重新将数据发送到之前的线程处理
            final Message reply = Message.obtain();
            reply.copyFrom(msg);
            reply.setTarget(replyHandler);
            replyHandler.post(new Runnable() {
                @Override
                public void run() {
                    onPostComplete(reply);
                }
            });
        }
    }

    public CCAsyncHandler() {
        super();
        synchronized (CCAsyncHandler.class) {
            HandlerThread thread = new HandlerThread(TAG);
            thread.start();

            mWorkLooper = thread.getLooper();
        }
        mWorkThreadHandler = createHandler(mWorkLooper);
    }

    protected Handler createHandler(Looper looper) {
        return new CCWorkHandler(looper, this);
    }

    /**
     * 移除还未开始的操作
     * @param what  msg.what
     */
    public void cancelOperation(int what) {
        mWorkThreadHandler.removeMessages(what);
    }

    /**
     * 移除还未开始的操作
     * @param what msg.what
     * @param obj  msg.obj
     */
    public void cancelOperation(int what, Object obj) {
        mWorkThreadHandler.removeMessages(what, obj);
    }

    /**
     * 发送消息
     * @param asyncMSg
     */
    public void sendAsyncMessage(final Message asyncMSg) {
        sendAsyncMessageDelay(asyncMSg, 0);
    }

    /**
     * 延时发送异步消息
     * @param asyncMSg          消息内容
     * @param delayMillis  多少时长后发送
     */
    public void sendAsyncMessageDelay(final Message asyncMSg, long delayMillis) {
        final Message workMsg = Message.obtain();
        workMsg.copyFrom(asyncMSg);
        mWorkThreadHandler.sendMessageDelayed(workMsg, delayMillis);
    }

    /**
     * 延时循环发送异步消息
     * @param asyncMSg            消息内容等
     * @param delayMillis     多少时长后发送
     * @param intervalMillis  间隔多少时长
     */
    public void sendScheduleAsyncMessage(final Message asyncMSg, final long delayMillis, final long intervalMillis) {
        mWorkThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendAsyncMessage(asyncMSg);

                mWorkThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        sendScheduleAsyncMessage(asyncMSg, intervalMillis, intervalMillis);
                    }
                });
            }
        }, delayMillis);
    }

    /**
     * 异步处理消息，该消息中所有参数将原本返回至onPostComplete中，也可以在某些处理完成后重赋值该asyncMsg，
     * 如果需要监听处理完成的方法，请重写onPostComplete
     * @param asyncMsg
     */
    protected void onAsyncDeal(final Message asyncMsg) {

    }

    /**
     * 异步处理完成后执行方法
     * @param msg
     */
    protected void onPostComplete(final Message msg) {

    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
//        onAsyncComplete(msg);
    }
}
