package com.example.liuliangqi.CP.album;

import android.content.Context;
import android.util.DisplayMetrics;


public class CommonUtil {

    public static int getElementSzie(Context context) {
        if (context != null) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            int screenHeight = px2dip(dm.heightPixels, context);
            int screenWidth = px2dip(dm.widthPixels, context);
            int size = screenWidth / 6;
            if (screenWidth >= 800) {
                size = 60;
            } else if (screenWidth >= 650) {
                size = 55;
            } else if (screenWidth >= 600) {
                size = 50;
            } else if (screenHeight <= 400) {
                size = 20;
            } else if (screenHeight <= 480) {
                size = 25;
            } else if (screenHeight <= 520) {
                size = 30;
            } else if (screenHeight <= 570) {
                size = 35;
            } else if (screenHeight <= 640) {
                if (dm.heightPixels <= 960) {
                    size = 35;
                } else if (dm.heightPixels <= 1000) {
                    size = 45;
                }
            }
            return size;
        }
        return 40;
    }

    private static int px2dip(float pxValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getImageMessageItemDefaultWidth(Context context) {
        return CommonUtil.getElementSzie(context) * 5;
    }

    public static int getImageMessageItemDefaultHeight(Context context) {
        return CommonUtil.getElementSzie(context) * 7;
    }

    public static int getImageMessageItemMinWidth(Context context) {
        return CommonUtil.getElementSzie(context) * 3;
    }

    public static int getImageMessageItemMinHeight(Context context) {
        return CommonUtil.getElementSzie(context) * 3;
    }

}
