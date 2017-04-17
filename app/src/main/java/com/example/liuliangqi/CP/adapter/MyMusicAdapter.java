package com.example.liuliangqi.CP.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liuliangqi.CP.R;
import com.example.liuliangqi.CP.Utils.MusicUtils;
import com.example.liuliangqi.CP.bean.Music;
import com.example.liuliangqi.CP.bean.Player;

import java.util.List;


public class MyMusicAdapter extends BaseAdapter {
    private final Context mContext;
    private List<Music> datas = null;
    private TextView title, artist, time;
    private ImageView albumbg;
    private Player player = Player.getPlayer();

    public MyMusicAdapter(Context context, List<Music> datas){
        this.mContext = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int arg0) {
        return datas.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return datas.get(position).getId();
    }

    static class ViewHolder {
        TextView tv_title;
        TextView tv_artist;
        TextView tv_time;
        ImageView img_collect;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View
                    .inflate(mContext, R.layout.music_list_items_function_bar, null);
            holder = new ViewHolder();

            title = (TextView) convertView.findViewById(R.id.list_item_title);
            artist = (TextView) convertView.findViewById(R.id.list_item_artist);
            albumbg = (ImageView) convertView.findViewById(R.id.list_img_button);
            time = (TextView) convertView.findViewById(R.id.list_item_time);
            setMarquee(title);
            setMarquee(artist);
            holder.tv_title = title;
            holder.tv_artist = artist;
            holder.img_collect = albumbg;
            holder.tv_time = time;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_title.setText(datas.get(position).getTitle());
        holder.tv_artist.setText(datas.get(position).getArtist());
        holder.img_collect.setImageBitmap(datas.get(position).getAlbum());
        holder.tv_time.setText(MusicUtils.formatTime(datas.get(position).getDuration()));
        return convertView;
    }

    private void setMarquee(TextView view){
        if(view != null){
            view.setEllipsize(TextUtils.TruncateAt.END);
            view.setSingleLine(true);
            view.setSelected(true);
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
        }
    }
}
