package com.example.liuliangqi.CP.fragments;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liuliangqi.CP.Config.Config;
import com.example.liuliangqi.CP.R;
import com.example.liuliangqi.CP.Utils.MusicUtils;
import com.example.liuliangqi.CP.adapter.MyMusicAdapter;
import com.example.liuliangqi.CP.bean.Music;
import com.example.liuliangqi.CP.bean.Player;
import com.example.liuliangqi.CP.service.PlayerService;

import java.util.List;


/**
 * Created by liuliangqi on 2017/4/2.
 */

public class PlayFragment extends Fragment implements View.OnClickListener{
    private ListView mListView;
    private List<Music> list;
    private MyMusicAdapter adapter;

    private Player player = Player.getPlayer();

    //中间人对象
    private PlayerService mPlayerService;
    private final MyConn conn = new MyConn();

    //获取控件
    private Button btnNext, btnPrev, btnPlay;
    private ImageView mImg, mLoop;
    private TextView mTvTitle, mTvArtist;
    private SeekBar seekBar;
    private final SeekHandle mSeekHandle = new SeekHandle();
    private final SeekThread mSeekThread = new SeekThread();
    private final MusicChangeReceiver changeReceiver = new MusicChangeReceiver();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        bindService();
        registerBroadcast();
        //初始化界面
        View view = inflater.inflate(R.layout.music_main, container, false);

        initView(view);
        initBottomBar(view);
        initSeekBar(view);
        return view;
    }


    public void registerBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.RECEIVER_MUSIC_CHANGE);
        MainActivity.getAppContext().registerReceiver(changeReceiver, filter);
    }


    private void bindService(){
        //获取Service
        Intent startIntent = new Intent(getActivity(), PlayerService.class);
        getActivity().startService(startIntent);
        //Bind Service
        getActivity().bindService(startIntent, conn, Context.BIND_AUTO_CREATE);
    }

    //初始化listView
    public void initView(View parentView){
        mListView = (ListView) parentView.findViewById(R.id.lv_musics);

        list = MusicUtils.getMusicData(parentView.getContext());
        adapter = new MyMusicAdapter(parentView.getContext(), list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPlayerService.play(i);
                player.setPosition(i);
                refreshBottomBar();

            }
        });

    }


    @Override
    public void onDestroy() {
        //解绑服务
        getActivity().unbindService(conn);
        MainActivity.getAppContext().unregisterReceiver(changeReceiver);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshBottomBar();
    }

    @Override
    public void onClick(View view) {
        int pos = player.getPosition();
        switch (view.getId()) {
            case R.id.bottom_btn_next:
                player.setPosition(pos++);
                mListView.setSelection(pos);
                mPlayerService.next();
                break;
            case R.id.bottom_btn_previous:
                player.setPosition(pos--);
                mListView.setSelection(pos);
                mPlayerService.previous();
                break;
            case R.id.bottom_btn_play:
                if ( player.getPlaying() == Config.PLAYING_PLAY) {
                    mPlayerService.pause();
                } else if (player.getPlaying() == Config.PLAYING_PAUSE) {
                    mPlayerService.replay();
                } else {
                    mPlayerService.play(pos);
                }
                break;
            case R.id.bottom_btn_loop:

                switch (player.getMode()){
                    case Config.MODE_REPEAT_SINGLE:
                        player.setMode(Config.MODE_REPEAT_ALL);
                        mLoop.setImageResource(getImgLoopBg(player.getMode()));
                        break;
                    case Config.MODE_REPEAT_ALL:
                        player.setMode(Config.MODE_SEQUENCE);
                        mLoop.setImageResource(getImgLoopBg(player.getMode()));
                        break;
                    case Config.MODE_SEQUENCE:
                        player.setMode(Config.MODE_RANDOM);
                        mLoop.setImageResource(getImgLoopBg(player.getMode()));
                        break;
                    case Config.MODE_RANDOM:
                        player.setMode(Config.MODE_REPEAT_SINGLE);
                        mLoop.setImageResource(getImgLoopBg(player.getMode()));
                        break;
                }
        }
        refreshBottomBar();
    }


    //初始化底部操作栏
    private void initBottomBar(View parentView){
        parentView.findViewById(R.id.bottom_bar).setOnClickListener(this);
        mImg = (ImageView) parentView.findViewById(R.id.bottom_img_image);
        mImg.setBackgroundResource(R.drawable.music);

        mLoop = (ImageView) parentView.findViewById(R.id.bottom_btn_loop);
        mLoop.setImageResource(R.drawable.bt_playing_mode_singlecycle);
        player.setMode(Config.MODE_REPEAT_SINGLE);

        mTvTitle = (TextView) parentView.findViewById(R.id.bottom_tv_title);
        mTvArtist = (TextView) parentView.findViewById(R.id.bottom_tv_artist);
        mTvTitle.setText(Config.TITLE);
        mTvArtist.setText(Config.ARTIST);

        btnNext = (Button) parentView.findViewById(R.id.bottom_btn_next);
        btnPrev = (Button) parentView.findViewById(R.id.bottom_btn_previous);
        btnPlay = (Button) parentView.findViewById(R.id.bottom_btn_play);

        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        mLoop.setOnClickListener(this);
    }

    private void initSeekBar(View parent){
        mSeekHandle.post(mSeekThread);
        seekBar = (SeekBar) parent.findViewById(R.id.music_seekbar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(seekBar.getProgress());
            }
        });
    }

    class SeekThread implements Runnable{

        @Override
        public void run() {
            Message msg = Message.obtain();
            msg.arg1 = player.getDuration();
            msg.arg2 = player.getCurrentPosition();
            mSeekHandle.sendMessage(msg);
        }
    }

    class SeekHandle extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            seekBar.setMax(msg.arg1);
            seekBar.setProgress(msg.arg2);

            mSeekHandle.post(mSeekThread);
        }
    }
    //当每次重新加载界面的时候刷新显示
    private void refreshBottomBar(){
        switch (player.getPlaying()){
            case Config.PLAYING_PAUSE:
                mImg.setImageResource(R.drawable.music);
                btnPlay.setBackgroundResource(R.drawable.selector_btn_play);
                mTvTitle.setText(player.getMusic().getTitle());
                mTvArtist.setText(player.getMusic().getArtist());
                break;
            case Config.PLAYING_PLAY:
                mImg.setImageResource(R.drawable.music);
                btnPlay.setBackgroundResource(R.drawable.selector_btn_pause);
                mTvTitle.setText(player.getMusic().getTitle());
                mTvArtist.setText(player.getMusic().getArtist());
                break;
            case Config.PLAYING_STOP:
                mImg.setImageResource(R.drawable.music);
                btnPlay.setBackgroundResource(R.drawable.selector_btn_play);
                mTvTitle.setText(Config.TITLE);
                mTvArtist.setText(Config.ARTIST);
        }
    }


    //监听服务的状态
    private class MyConn implements ServiceConnection{
        //当服务连接成功
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            //获取中间人对象
            mPlayerService = ((PlayerService.MyBinder)iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast toast = Toast.makeText(MainActivity.getAppContext(), "音乐播放失败，退出再进试试", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            mPlayerService = null;
        }
    }



    private static final int[] loopModes = {
            R.drawable.bt_playing_mode_singlecycle,
            R.drawable.bt_playing_mode_cycle,
            R.drawable.bt_playing_mode_order,
            R.drawable.bt_playing_mode_shuffle };

    /**
     * 获取循环播放控件背景
     */
    public int getImgLoopBg(int mode) {
        player.setMode(mode);
        return loopModes[mode];
    }


    public class MusicChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Config.RECEIVER_MUSIC_CHANGE.equals(intent.getAction())) {
                if (Player.getPlayer().getPlaying() != Config.PLAYING_STOP) {
                    refreshBottomBar();
                }
            }
        }
    }
}
