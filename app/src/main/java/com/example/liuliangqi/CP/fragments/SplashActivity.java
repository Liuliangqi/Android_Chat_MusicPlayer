package com.example.liuliangqi.CP.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.liuliangqi.CP.R;

/**
 * Created by liuliangqi on 2017/4/3.
 */

public class SplashActivity extends Activity {
    private ImageView backGround;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        initView();

    }

    private void initView(){
        backGround = (ImageView) findViewById(R.id.splash);


        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) backGround.getLayoutParams();
        layoutParams.width = 400;
        layoutParams.height = 400;
        layoutParams.leftMargin = metrics.widthPixels / 2 - layoutParams.width / 2;
        layoutParams.topMargin = metrics.heightPixels / 2 - layoutParams.height / 2;

        backGround.setLayoutParams(layoutParams);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 2000);
    }




}
