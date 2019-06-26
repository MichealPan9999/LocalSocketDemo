package com.panzq.applicationb;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 解决内存泄漏的Handler
 *
 * @author hq
 */
public class SafeHandler extends Handler {
    public interface Callback {
        /**
         * 消息处理回调
         *
         * @param msg 消息
         */
        void handlerMessage(Message msg);
    }

    private WeakReference<Callback> cbRef;

    public SafeHandler(SafeHandler.Callback cb) {
        cbRef = new WeakReference<>(cb);
    }

    @Override
    public void handleMessage(Message msg) {
        Callback cb = cbRef.get();
        if (cb != null) {
            cb.handlerMessage(msg);
        }
    }

    /**
     * 发送消息。在发送前会先清空，避免消息栈中包括多个相同的
     *
     * @param what        需要发送的消息
     * @param delayMillis 延迟时间
     */
    public void sendMessageOnly(int what, int delayMillis) {
        removeMessages(what);
        super.sendEmptyMessageDelayed(what, delayMillis);
    }

    /**
     * 从消息队列中移除多个消息
     *
     * @param messages 需要移除的消息列表
     */
    public void removeMessages(int... messages) {
        if (messages == null) {
            return;
        }
        for (int msg : messages) {
            removeMessages(msg);
        }
    }

    public void clear() {
        removeCallbacksAndMessages(null);
    }
}
