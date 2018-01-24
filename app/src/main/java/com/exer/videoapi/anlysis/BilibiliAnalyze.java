package com.exer.videoapi.anlysis;

import com.exer.videoapi.NetVideo;
import com.exer.widgets.Tools;
import com.exer.widgets.VideoUrlItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.exer.videoapi.NetVideoHelper.VideoUrlTypr;

/**
 * Created by HeHang on 2018/1/18.
 */

public class BilibiliAnalyze {
    public static List<NetVideo> GetVideoListByHome(String retStr){
        List<NetVideo> list =  new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(retStr).getJSONObject("hot");
            JSONArray streams = (JSONArray) jsonObject.get("list");
            for (int i = 0; i < streams.length(); i++){
                try {
                    JSONObject stream = streams.getJSONObject(i);
                    String title = stream.getString("title");
                    String info = stream.getString("author");
                    String imgUrl = stream.getString("pic");
                    String videoInfo = "http://www.bilibili.com/video/av" + stream.getString("aid");
                    String number = "" + i;
                    NetVideo nv = new NetVideo(title,info,imgUrl,videoInfo,number);
                    list.add(nv);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<NetVideo> getNetVideoList(String retStr){
        List<NetVideo> list =  new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(retStr).getJSONObject("data").getJSONObject("items");
            JSONArray streams = (JSONArray) jsonObject.get("archive");
            for (int i = 0; i < streams.length(); i++){
                try {
                    JSONObject stream = streams.getJSONObject(i);
                    String title = stream.getString("title");
                    String info = stream.getString("desc");
                    String imgUrl = stream.getString("cover").replaceFirst("https","http");
                    String videoInfo = stream.getString("uri").replace("bilibili://video/", "http://www.bilibili.com/video/av");;
                    NetVideo nv = new NetVideo(title,info,imgUrl,videoInfo,"" + (i+1));
                    list.add(nv);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<VideoUrlItem> GetVideoUrlList(String retStr){
        List<VideoUrlItem> list = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(retStr).getJSONObject("data");
            JSONArray streams = (JSONArray) jsonObject.get("streams");
            for (int i = 0; i < streams.length(); i++){
                try {
                    JSONObject stream = streams.getJSONObject(i);
                    String des = VideoUrlTypr.get(stream.getString("quality"));
                    JSONArray ja = (JSONArray)(stream.get("segs"));
                    String url = ja.getJSONObject(0).getString("url");
                    VideoUrlItem vui = new VideoUrlItem(des,url );
                    list.add(vui);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }
    public static String getSearchUrl(String searchParam){
        String params = "appkey=1d8b6e7d45233436" + "&build=518000&keyword=" + URLEncoder.encode(searchParam) + "&mobi_app=android&platform=android&pn=1" + "&ps=20&ts=" + System.currentTimeMillis() / 1000L;
        return "http://app.bilibili.com/x/v2/search?" + params + "&sign=" + Tools.md5Encoder(new StringBuilder().append(params).append("560c52ccd288fed045859ed18bffd973").toString());
    }
}
