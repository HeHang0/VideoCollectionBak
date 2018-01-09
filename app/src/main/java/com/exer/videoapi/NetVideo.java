package com.exer.videoapi;


import android.graphics.Bitmap;

public class NetVideo {
    private String title;
    private String info;
    private Bitmap img;
    private String videoInfo;

    public NetVideo(String title, String info, Bitmap img, String videoInfo){
        this.title = title;
        this.info = info;
        this.img = img;
        this.videoInfo = videoInfo;
    }

    public String getTitle() {
        return title;
    }

    public String getInfo() {
        return info;
    }

    public Bitmap getImg() {
        return img;
    }

    public String getVideoUrl() {
        return videoInfo;
    }
}
