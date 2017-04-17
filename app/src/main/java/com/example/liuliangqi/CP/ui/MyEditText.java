package com.example.liuliangqi.CP.ui;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.liuliangqi.CP.R;

/**
 * Created by liuliangqi on 2017/4/7.
 */

public class MyEditText extends android.support.v7.widget.AppCompatEditText {

    private Context mContext;
    private Drawable img_del_Blue;
    private Rect rect;

    public MyEditText(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }


    public void init(){
        img_del_Blue = mContext.getResources().getDrawable(R.drawable.delete_blue);

        setDrawable();

        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setDrawable();
            }
        });


    }

    public void setDrawable() {
        if(this.length() > 0){
            this.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    img_del_Blue, null);
        }else{
            this.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (img_del_Blue != null && event.getAction() == MotionEvent.ACTION_UP) {
            rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 120;
            int eventX = (int) event.getRawX();//距离屏幕的距离
            int eventY = (int) event.getRawY();
            if(rect.contains(eventX, eventY)) {
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.img_del_Blue = null;
        this.rect = null;
    }

    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        if(right != null){
            this.img_del_Blue = right;
        }
        super.setCompoundDrawables(left, top, right, bottom);
    }
}
