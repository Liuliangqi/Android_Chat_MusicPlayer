package com.example.liuliangqi.CP.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.android.adapter.CommonBaseAdapter;
import com.baidu.android.itemview.helper.IData;
import com.baidu.android.itemview.helper.IStyle;
import com.example.liuliangqi.CP.Config.ConstantKeys;
import com.example.liuliangqi.CP.bean.ImageItem;
import com.example.liuliangqi.CP.view.ImageGridSingleTypeView;

public class ImageGridAdapter extends CommonBaseAdapter<ImageItem> {
    
    public static final int MESSAGE_TYPE_INVALID = -1;
    private static final int VIEW_TYPE_COUNT = 9;
    
    public ImageGridAdapter(Context arg0) {
        super(arg0);
    }

    @Override
    protected View generateView(int position, View convertView, ViewGroup viewGroup) {
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case ConstantKeys.ALBUM_IMAGE_GRIDVIEW_DISPLAYTYPE_SINGLE:
                    convertView = new ImageGridSingleTypeView(mContext);
                    break;

                default:
                    break;
            }
        }

        IStyle item = mData.get(position);
        if (item != null) {
//            Log.e("fff", "imgpath:"+((ImageItem)item).getImagePath());
            IData<IStyle> data = (IData<IStyle>) convertView;
            data.setData(item);
        }
        return convertView;
    }
    @Override
    public int getItemViewType(int position) {

        if (position >= mData.size()) {
            return MESSAGE_TYPE_INVALID;
        }
        return getItem(position).getStyle();
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}
