package com.example.liuliangqi.CP.bean;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

/**
 * Created by liuliangqi on 2017/4/4.
 */

public class Music implements Serializable {
    private int id;
    private String title;
    private String artist;
    private String path;
    private String size;
    private Bitmap album;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private int duration;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Bitmap getAlbum() {
        return album;
    }

    public void setAlbum(Bitmap album) {
        this.album = album;
    }
}
