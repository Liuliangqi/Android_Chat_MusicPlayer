package com.example.liuliangqi.CP.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.widget.Toast;

import com.example.liuliangqi.CP.Config.Config;
import com.example.liuliangqi.CP.bean.Music;
import com.example.liuliangqi.CP.bean.Player;
import com.example.liuliangqi.CP.fragments.MainActivity;


/**
 * Created by liuliangqi on 2017/4/4.
 */

public class PlayerService extends Service {
    private final Player mPlayer = Player.getPlayer();
    private final MyBinder myBinder = new MyBinder();

    //服务销毁的时候把播放器也销毁，并设置为停止播放状态
    @Override
    public void onDestroy() {
        if(mPlayer != null){
           mPlayer.destroy();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }





    public class MyBinder extends Binder{
        public PlayerService getService(){
            return PlayerService.this;
        }
    }


    //播放
    public Music play(int position){
        Music music = null;
        music = mPlayer.play(position);
        return music;
    }

    //默认播放
    public Music play(){
        Music music = null;
        if(mPlayer.getList() == null || mPlayer.getList().isEmpty()){
            Toast toast = Toast.makeText(MainActivity.getAppContext(), "您手机中没有音乐", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }else{
            music = mPlayer.play(0);
        }
        return music;
    }


    //暂停
    public void pause(){
        mPlayer.pause();
    }


    //replay
    public void replay(){
        mPlayer.replay();
    }


    //下一首
    public Music next(){
        Music music = null;
        if(mPlayer.getPlaying() == Config.PLAYING_STOP || mPlayer.getList().isEmpty()){
            play();
        }else{
            music = mPlayer.next();
        }

        return music;
    }

    //上一首
    public Music previous(){
        Music music = null;
        if(mPlayer.getPlaying() == Config.PLAYING_STOP || mPlayer.getList().isEmpty()){
            play();
        }else{
            music = mPlayer.previous();
        }
        return music;
    }
}
