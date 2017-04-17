package com.example.liuliangqi.CP.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.liuliangqi.CP.R;

/**
 * Created by liuliangqi on 2017/4/6.
 */

public class MsgHeader extends LinearLayout {

    private LinearLayout mContainer;
    private ProgressBar mProgressBar;
    private TextView mHintTextView;
    private int mState = STATE_NORMAL;

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;
    public MsgHeader(Context context) {
        super(context);
        initView(context);
    }

    public MsgHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    //初始化布局
    private void initView(Context context){
        //初始情况下，下拉刷新的view高度是0
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
        //获取到容器
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.chat_message_header, null);
        //将mContainer加入到当前的布局中
        addView(mContainer, layoutParams);
        setGravity(Gravity.BOTTOM);

        mHintTextView = (TextView) findViewById(R.id.msg_header_hint_textview);
        mProgressBar = (ProgressBar) findViewById(R.id.msg_header_progressbar);
    }

    //设置下拉菜单progressbar的状态
    public void setState(int state){
        if(state == mState){
            return;
        }
        //显示刷新进度
        if(state == STATE_REFRESHING){
            mProgressBar.setVisibility(View.VISIBLE);
        }else{
            //否则显示下拉箭头，准备刷新
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        switch (state){
            case STATE_NORMAL:
                if(mState == STATE_READY){}
                if(mState == STATE_REFRESHING){}
                //显示刷新字
                mHintTextView.setVisibility(View.VISIBLE);
                mHintTextView.setText("显示更多消息");
                break;
            case STATE_READY:
                if(mState != STATE_READY){
                    mHintTextView.setVisibility(View.VISIBLE);
                    mHintTextView.setText("释放即可显示");
                }
                break;
            case STATE_REFRESHING:
                mHintTextView.setVisibility(View.GONE);
                break;
            default:
        }
        mState = state;
    }


    //设置显示高度
    public void setVisiableHeight(int height){
        if(height < 0){
            height = 0;
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mContainer.getLayoutParams();
        layoutParams.height = height;
        mContainer.setLayoutParams(layoutParams);
    }



    public int getVisiableHeight(){
        return mContainer.getHeight();
    }
}
