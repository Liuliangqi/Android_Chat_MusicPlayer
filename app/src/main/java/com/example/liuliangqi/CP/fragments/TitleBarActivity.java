package com.example.liuliangqi.CP.fragments;

import android.view.View;

import com.baidu.android.activity.BaseActivity;
import com.example.liuliangqi.CP.R;
import com.example.liuliangqi.CP.view.TitleBar;

public abstract class TitleBarActivity extends BaseActivity implements
        TitleBar.OnClickListener {

    private TitleBar mTitleBar;

    private void initTitleBar() {
        mTitleBar = (TitleBar) findViewById(R.id.titleBar);
        mTitleBar.setOnClickListener(this);
    };

    public void setTitleLeft(View view) {
        checkTitleBar();
        mTitleBar.setLeft(view);
    }

    public void setTitleLeft(View view, int gravity) {
        checkTitleBar();
        mTitleBar.setLeft(view, gravity);
    }

    public void setTitleMiddle(View view) {
        checkTitleBar();
        mTitleBar.setMiddle(view);
    }

    public void setTitleRight(View view) {
        checkTitleBar();
        mTitleBar.setRight(view);
    }

    public void setTitleRight(View view, int gravity) {
        checkTitleBar();
        mTitleBar.setRight(view, gravity);
    }

    private void checkTitleBar() {
        if (null == mTitleBar) {
            initTitleBar();
        }
    }

    @Override
    public void onClickLeft() {
        finish();
    }

    @Override
    public void onClickMiddle() {
        // do nothing
    }

    @Override
    public void onClickRight() {
        // do nothing
    }
}
