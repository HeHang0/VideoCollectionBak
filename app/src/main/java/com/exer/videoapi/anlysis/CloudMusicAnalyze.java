package com.exer.videoapi.anlysis;

import com.exer.videoapi.NetVideo;
import com.exer.widgets.Tools;
import com.exer.widgets.VideoUrlItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.exer.videoapi.NetVideoHelper.VideoUrlTypr;


public class CloudMusicAnalyze {
    public static List<NetVideo> GetVideoListByHome(String retStr){
        return getList(retStr);
    }

    public static List<NetVideo> GetNetVideoList(String retStr){
        return getList(retStr);
    }

    private static List<NetVideo> getList(String retStr){
        List<NetVideo> list =  new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(retStr).getJSONObject("result");
            JSONArray videos = (JSONArray) jsonObject.get("videos");
            for (int i = 0; i < videos.length(); i++){
                try {
                    JSONObject video = videos.getJSONObject(i);
                    String title = video.getString("title");
                    String info = ((JSONArray)video.get("creator")).getJSONObject(0).getString("userName");
                    String imgUrl = video.getString("coverUrl");
                    String videoInfo = video.getString("vid");
                    NetVideo nv = new NetVideo(title,info,imgUrl,videoInfo,""+i);
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
            JSONObject videoUrls = new JSONObject(retStr).getJSONObject("data").getJSONObject("brs");
            Iterator<String> items = videoUrls.keys();
            while(items.hasNext()){
                String item = items.next();
                VideoUrlItem vui = new VideoUrlItem(VideoUrlTypr.get(item),videoUrls.getString(item));
                list.add(vui);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<VideoUrlItem> GetVideoUrlListSingle(String retStr){
        List<VideoUrlItem> list = new ArrayList<>();
        try {
            JSONObject videoUrl = ((JSONArray)(new JSONObject(retStr).getJSONArray("urls"))).getJSONObject(0);
                VideoUrlItem vui = new VideoUrlItem(VideoUrlTypr.get(videoUrl.getString("r")),videoUrl.getString("url"));
                list.add(vui);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static String getApiByCloudVideoId(String vid){
        try {
            String param = Tools.aesEncrypt("{\"ids\":\"[\\\"" + vid + "\\\"]\",\"resolution\":\"1080\",\"csrf_token\":\"\"}", "0CoJUm6Qyw8W8jud");
            param = Tools.aesEncrypt(param, "a8LWv2uAtXjzSfkQ");
            param = java.net.URLEncoder.encode(param,   "utf-8");
            String encSecKey = "&encSecKey=2d48fd9fb8e58bc9c1f14a7bda1b8e49a3520a67a2300a1f73766caee29f2411c5350bceb15ed196ca963d6a6d0b61f3734f0a0f4a172ad853f16dd06018bc5ca8fb640eaa8decd1cd41f66e166cea7a3023bd63960e656ec97751cfc7ce08d943928e9db9b35400ff3d138bda1ab511a06fbee75585191cabe0e6e63f7350d6";
            String url = "http://music.163.com/weapi/cloudvideo/playurl?csrf_token=";
            String paramData = "params=" + param + encSecKey;
            return url + "&" + paramData;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
