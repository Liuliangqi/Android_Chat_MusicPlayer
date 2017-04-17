package com.example.liuliangqi.CP.bean;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Gravity;
import android.widget.Toast;

import com.example.liuliangqi.CP.Config.Config;
import com.example.liuliangqi.CP.Utils.MusicUtils;
import com.example.liuliangqi.CP.fragments.MainActivity;

import java.util.List;

/**
 * Created by liuliangqi on 2017/4/5.
 */

public class Player {
    private static Player localPlayer = new Player();
    //初始是循环所有
    private int mode = Config.MODE_REPEAT_ALL;
    //初始是停止播放状态
    private int playing = Config.PLAYING_STOP;
    private int position = 0;

    //获取media player
    private MediaPlayer player;


    private Music music;

    List<Music> list;

    private Player() {
        if (list == null) {
            list = MusicUtils.getMusicData(MainActivity.getAppContext());
        }
    }

    public static Player getPlayer() {
        return localPlayer;
    }

    public Music getMusic() {
        Music music = null;
        if (position >= list.size()) {
            music = new Music();
            music.setArtist(Config.ARTIST);
            music.setTitle(Config.TITLE);
        } else {
            music = list.get(position);
        }

        return music;
    }

    public int getDuration() {
        int durat = 0;
        if (player != null) {
            durat = player.getDuration();
        }
        return durat;
    }

    public int getMode() {
        return mode;
    }

    public int getCurrentPosition() {
        int currentPosition = 0;
        if (player != null) {
            currentPosition = player.getCurrentPosition();
        }
        return currentPosition;
    }


    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getPlaying() {
        return playing;
    }


    public List<Music> getList() {
        return list;
    }


    public void setPosition(int position) {
        this.position = position;
    }
    public int getPosition(){
        return position;
    }

    public Music play(int position) {
        if (list == null || list.isEmpty()) {
            Toast toast = Toast.makeText(MainActivity.getAppContext(), "您手机中没有音乐", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            if (playing == Config.PLAYING_PLAY) {
                player.reset();
            }
            player = MediaPlayer.create(MainActivity.getAppContext(), Uri.parse(list.get(position).getPath()));
            //记录播放的位置，为next准备
            this.position = position;
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    MainActivity.getAppContext().sendBroadcast(new Intent(Config.RECEIVER_MUSIC_CHANGE));
                    completion(Player.this.list, Player.this.position);
                }
            });
            playing = Config.PLAYING_PLAY;
            MainActivity.getAppContext().sendBroadcast(new Intent(Config.RECEIVER_MUSIC_CHANGE));
        }
        return list.get(position);
    }


    //暂停播放
    public void pause() {
        if (playing != Config.PLAYING_PAUSE) {
            player.pause();
            playing = Config.PLAYING_PAUSE;
            MainActivity.getAppContext().sendBroadcast(new Intent(Config.RECEIVER_MUSIC_CHANGE));
        }
    }


    //重新播放, 要返回当前正在播放的歌曲
    public Music replay() {
        if (playing != Config.PLAYING_PLAY) {
            player.start();
            playing = Config.PLAYING_PLAY;
            MainActivity.getAppContext().sendBroadcast(new Intent(Config.RECEIVER_MUSIC_CHANGE));
        }
        return list.get(position);
    }

    //播放下一首
    public Music next() {
        if (list.size() < 1) {
            destroy();
        } else {
            player.reset();//停止上一首
            position = (position + 1) % list.size();
            play(position);
        }
        return list.get(position);
    }

    public void destroy() {
        if (player != null) {
            player.release();
            playing = Config.PLAYING_STOP;
        }
    }

    public void stop() {
        if (playing != Config.PLAYING_STOP) {
            player.stop();
            playing = Config.PLAYING_STOP;
            MainActivity.getAppContext().sendBroadcast(new Intent(Config.RECEIVER_MUSIC_CHANGE));
        }
    }

    public Music previous() {
        Music music = null;
        if (list.size() < 1) {
            destroy();
            music = null;
        } else {
            player.reset(); // 停止上一首
            position = (position + list.size() - 1) % list.size();
            play(position);
            music = list.get(position);
        }
        return music;
    }

    public Music completion(List<Music> list, int position) {
        Music music = null;
        switch (mode) {
            case Config.MODE_REPEAT_SINGLE:
                //单曲播放
                stop();
                break;
            case Config.MODE_REPEAT_ALL:
                //单曲循环
                music = play(position);
                break;
            case Config.MODE_SEQUENCE:
                //列表循环
                music = play((position + 1) % list.size());
                break;
            case Config.MODE_RANDOM:
                music = play((int) (Math.random() * list.size()));
                break;
            default:
                break;
        }
        //返回选中的歌曲
        return music;
    }

    //跳转音乐
    public void seekTo(int msec) {
        if (player != null) {
            player.seekTo(msec);
        }
    }
}
