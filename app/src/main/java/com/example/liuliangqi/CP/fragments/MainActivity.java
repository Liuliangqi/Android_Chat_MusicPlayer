package com.example.liuliangqi.CP.fragments;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.liuliangqi.CP.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener{

    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mDatas;
    private TextView mChatTextView;
    private TextView mContactTextView;
    private TextView mPlayTextView;
    private LinearLayout mChatLinearLayout;
    private LinearLayout mPlayLinearLayout;
    private LinearLayout mContactLinearLayout;
    private ImageView mTabline;

    private int mScreen1_3;
    private int mCurrentPageIndex;

    private static Context instance;
    private static MainActivity mainActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = getApplicationContext();
        mainActivity = this;
        setContentView(R.layout.activity_main);
        initTabLine();
        initView();
    }

    private void initTabLine(){
        mTabline = (ImageView) findViewById(R.id.id_tv_tabline);
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        mScreen1_3 = outMetrics.widthPixels / 3;
        ViewGroup.LayoutParams layoutParams = mTabline.getLayoutParams();
        layoutParams.width = mScreen1_3;
        mTabline.setLayoutParams(layoutParams);
    }
    private void initView(){
        //find view
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mChatTextView = (TextView) findViewById(R.id.id_tv_chat);
        mContactTextView = (TextView) findViewById(R.id.id_tv_contact);
        mPlayTextView = (TextView) findViewById(R.id.id_tv_play);
        mChatLinearLayout = (LinearLayout) findViewById(R.id.id_ll_chat);
        mContactLinearLayout = (LinearLayout) findViewById(R.id.id_ll_contact);
        mPlayLinearLayout = (LinearLayout) findViewById(R.id.id_ll_play);


        //set onclick
        mChatLinearLayout.setOnClickListener(this);
        mContactLinearLayout.setOnClickListener(this);
        mPlayLinearLayout.setOnClickListener(this);
        mPlayLinearLayout.canScrollHorizontally(200);

        //create pages
        mDatas = new ArrayList<>();

        ChatFragment tab01 = new ChatFragment();
        ContactFragment tab02 = new ContactFragment();
        PlayFragment tab03 = new PlayFragment();

        mDatas.add(tab01);
        mDatas.add(tab02);
        mDatas.add(tab03);

        //create page adapter
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mDatas.get(position);
            }

            @Override
            public int getCount() {
                return mDatas.size();
            }
        };

        mViewPager.setAdapter(mAdapter);


        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mTabline.getLayoutParams();
                if(mCurrentPageIndex == 0 && position == 0){
                    layoutParams.leftMargin = (int) (positionOffset * mScreen1_3 + mCurrentPageIndex * mScreen1_3);
                }else if(mCurrentPageIndex == 1 && position == 0){
                    layoutParams.leftMargin = (int) (mCurrentPageIndex * mScreen1_3 + (positionOffset - 1) * mScreen1_3);
                }else if(mCurrentPageIndex == 1 && position == 1){
                    layoutParams.leftMargin = (int) (mCurrentPageIndex * mScreen1_3 + positionOffset * mScreen1_3);
                }else if(mCurrentPageIndex == 2 && position == 1){
                    layoutParams.leftMargin = (int) (mCurrentPageIndex * mScreen1_3 + (positionOffset - 1) * mScreen1_3);
                }

                mTabline.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageSelected(int position) {
                resetTextView();
                switch(position){
                    case 0:
                        mChatTextView.setTextColor(Color.parseColor("#008000"));
                        break;
                    case 1:
                        mContactTextView.setTextColor(Color.parseColor("#008000"));
                        break;
                    case 2:
                        mPlayTextView.setTextColor(Color.parseColor("#008000"));
                        break;
                }

                mCurrentPageIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    protected void resetTextView(){
        mChatTextView.setTextColor(Color.BLACK);
        mContactTextView.setTextColor(Color.BLACK);
        mPlayTextView.setTextColor(Color.BLACK);
    }

    @Override
    public void onClick(View view) {
        int position;
        switch (view.getId()){
            case R.id.id_ll_chat:
                position = 0;
                break;
            case R.id.id_ll_contact:
                position = 1;
                break;
            case R.id.id_ll_play:
                position = 2;
                break;
            default:
                position = 0;
                break;
        }
        mViewPager.setCurrentItem(position);
    }


    public static Context getAppContext(){
        return instance;
    }

    public static MainActivity getMainActivity(){
        return mainActivity;
    }
}
