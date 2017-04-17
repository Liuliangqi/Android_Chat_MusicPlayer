package com.example.liuliangqi.CP.Config;

/**
 * Created by liuliangqi on 2017/4/3.
 */

public class Config{


    public static final String TITLE = "CPMusic";
    public static final String ARTIST = "Liangqi";

    /**
     * paly list repeat mode
     */
    public static final int MODE_REPEAT_SINGLE = 0;
    public static final int MODE_REPEAT_ALL = 1;
    public static final int MODE_SEQUENCE = 2;
    public static final int MODE_RANDOM = 3;


    /**
     * palyer state
     */
    public static final int PLAYING_STOP = 0;
    public static final int PLAYING_PAUSE = 1;
    public static final int PLAYING_PLAY = 2;

    /**
     * music change broadcast
     */
    public static final String RECEIVER_MUSIC_CHANGE = "net.kymjs.music.music_change";

}
