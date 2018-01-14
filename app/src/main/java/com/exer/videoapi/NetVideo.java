package com.exer.videoapi;


import android.graphics.Bitmap;

public class NetVideo {
    private String title;
    private String info;
    private Bitmap img;
    private String imgUrl;
    private String videoInfo;
    private String number;

    public NetVideo(String title, String info, String imgUrl, String videoInfo, String number){
        this.title = title;
        this.info = info;
        this.imgUrl = imgUrl;
        this.videoInfo = videoInfo;
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public String getInfo() {
        return info;
    }

    public String getVideoUrl() {
        return videoInfo;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public String getNumber() {
        return number;
    }
}
