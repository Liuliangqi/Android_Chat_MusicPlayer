package com.example.liuliangqi.CP.Utils;

import java.util.List;

import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;

import com.example.liuliangqi.CP.R;
import com.example.liuliangqi.CP.push_server.BaiduPush;
import com.example.liuliangqi.CP.service.PushApplication;


public class SendMsgAsyncTask {
    private BaiduPush mBaiduPush;
    private String mMessage;
    private Handler mHandler;
    private MyAsyncTask mTask;
    private String mUserId;
    private OnSendScuessListener mListener;
    private List<String> mMessageList;

    public interface OnSendScuessListener {
        void sendScuess();
    }

    public void setOnSendScuessListener(OnSendScuessListener listener) {
        this.mListener = listener;
    }

    Runnable reSend = new Runnable() {

        @Override
        public void run() {
            L.i("resend msg...");
            send();// 重发
        }
    };

    public SendMsgAsyncTask(String jsonMsg, String useId) {
        mBaiduPush = PushApplication.getInstance().getBaiduPush();
        mMessage = jsonMsg;
        mUserId = useId;
        mHandler = new Handler();
    }

    public SendMsgAsyncTask(List<String> messageList, String useId) {
        mBaiduPush = PushApplication.getInstance().getBaiduPush();
        mMessageList = messageList;
        mUserId = useId;
        mHandler = new Handler();
    }

    // 发送
    public void send() {
        if (NetUtil.isNetConnected(PushApplication.getInstance())) {// 如果网络可用
            mTask = new MyAsyncTask();
            mTask.execute();
        } else {
            T.showLong(PushApplication.getInstance(), R.string.net_error_tip);
        }
    }

    // 停止
    public void stop() {
        if (mTask != null)
            mTask.cancel(true);
    }

    class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... message) {
            String result = "";
            if (mMessageList != null && mMessageList.size() > 0) {
                for (int i = 0; i < mMessageList.size(); i++) {
                    if (TextUtils.isEmpty(mUserId))
                        result = mBaiduPush.PushMessage(mMessageList.get(i));
                    else
                        result = mBaiduPush.PushMessage(mMessageList.get(i),
                                mUserId);
                }

            } else {
                if (TextUtils.isEmpty(mUserId))
                    result = mBaiduPush.PushMessage(mMessage);
                else
                    result = mBaiduPush.PushMessage(mMessage, mUserId);
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            L.i("send msg result:" + result);
            if (result.contains(BaiduPush.SEND_MSG_ERROR)) {// 如果消息发送失败，则100ms后重发
                mHandler.postDelayed(reSend, 100);
            } else {
                if (mListener != null)
                    mListener.sendScuess();
            }
        }
    }
}
