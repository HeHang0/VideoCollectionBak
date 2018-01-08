package com.exer.videoapi;

/**
 * Created by HeHang on 2018/1/8.
 */

public class NetVideo {
    private String title;
    private String info;
    private String imgUrl;
    private String videoUrl;

    public NetVideo(String title, String info, String imgUrl, String videoUrl){
        this.title = title;
        this.info = info;
        this.imgUrl = imgUrl;
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
