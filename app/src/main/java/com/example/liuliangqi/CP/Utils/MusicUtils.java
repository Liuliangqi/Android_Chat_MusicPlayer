package com.example.liuliangqi.CP.Utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.example.liuliangqi.CP.R;
import com.example.liuliangqi.CP.bean.Music;
import com.example.liuliangqi.CP.fragments.MainActivity;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuliangqi on 2017/4/4.
 */

public class MusicUtils {
    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */

    private static final Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.getAppContext().getResources(),
            R.drawable.album);
    public static List<Music> getMusicData(Context context) {
        List<Music> list = new ArrayList<Music>();
        // 媒体库查询语句（写一个工具类MusicUtils）
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Music song = new Music();
                song.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                song.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                song.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                song.setSize("" + cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                if(getArtAlbum(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))) == null){
                    song.setAlbum(bitmap);
                }
                else
                    song.setAlbum(getArtAlbum(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))));

                if (Integer.parseInt(song.getSize()) > 1000 * 800) {
                    // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                    if (song.getTitle().contains("-")) {
                        String[] str = song.getTitle().split("-");
                        song.setArtist(str[0].trim());

                        if(str[1].contains(".")){
                            song.setTitle(str[1].trim().split("\\.")[0]);
                        }else
                            song.setTitle(str[1].trim());

                    }
                    list.add(song);
                }
            }
            // 释放资源
            cursor.close();
        }

        return list;
    }

    /**
     * 定义一个方法用来格式化获取到的时间
     */
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;

        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }

    }

    public static Bitmap getArtAlbum(long audioId){
        String str = "content://media/external/audio/media/" + audioId+ "/albumart";
        Uri uri = Uri.parse(str);
        ParcelFileDescriptor pfd = null;
        try {
            pfd = MainActivity.getAppContext().getContentResolver().openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            return null;
        }
        Bitmap bm;
        if (pfd != null) {
            FileDescriptor fd = pfd.getFileDescriptor();
            bm = BitmapFactory.decodeFileDescriptor(fd);
            return bm;
        }
        return null;
    }
}