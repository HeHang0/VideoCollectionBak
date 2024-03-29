package com.exer.videoapi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.exer.videoapi.anlysis.BilibiliAnalyze;
import com.exer.videoapi.anlysis.CloudMusicAnalyze;
import com.exer.videoapi.anlysis.IQiYiAnalyze;
import com.exer.videoapi.anlysis.LiveAnalyze;
import com.exer.videoapi.anlysis.QQLiveAnalyze;
import com.exer.videoapi.anlysis.YouKuAnalyze;
import com.exer.videoapi.anlysis.YouTubeAnalyze;
import com.exer.widgets.Tools;
import com.exer.widgets.VideoUrlItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HeHang on 2018/1/8.
 */
//https://bvd2.nl/8/download/download.php?url=https://www.youtube.com/watch?v=4iVW7OhZ0p8
public class NetVideoHelper {
    public static Map<String, String> VideoUrlTypr = new HashMap<String, String>(){
        {
            put("SPEED", "极速");
            put("SD", "标清");
            put("HD", "高清");
            put("SUPERHD", "超清");
            put("1080P", "1080P");
            put("240", "标清");
            put("480", "高清");
            put("720", "超清");
            put("720P", "超清");
            put("1080", "1080P");
            put("ORIGINAL", "1080P");
        }
    };
    public static Map<NetVideoFrom, String> WebViewUrlAPI = new HashMap<NetVideoFrom, String>(){
        {
            put(NetVideoFrom.Youku,    "http://www.youku.com");
            put(NetVideoFrom.QQLive,   "http://m.v.qq.com");
            put(NetVideoFrom.IQiYi,    "http://m.iqiyi.com");
            put(NetVideoFrom.Bilibili, "http://m.bilibili.com");
            put(NetVideoFrom.YouTube,  "https://m.youtube.com");
        }
    };
    private static Map<NetVideoFrom, String> HomeUrlAPI = new HashMap<NetVideoFrom, String>(){
        {
            put(NetVideoFrom.Youku,"http://www.youku.com");
            put(NetVideoFrom.QQLive,"http://m.v.qq.com");
            put(NetVideoFrom.IQiYi,"http://m.iqiyi.com/");
            put(NetVideoFrom.CloudMusic,"http://music.163.com/api/cloudsearch/get/web?s=jfla&limit=50&type=1014&offset=0");
            put(NetVideoFrom.Bilibili,"https://www.bilibili.com/index/catalogy/1-3day.json");
        }
    };
    private static Map<NetVideoFrom, String> searchAPI = new HashMap<NetVideoFrom, String>(){
        {
            //put(NetVideoFrom.Youku,"https://openapi.youku.com/v2/searches/video/by_keyword.json?count=20&client_id=53e6cc67237fc59a&page=1&keyword=%s");http://www.soku.com/m/y/video?q=
            put(NetVideoFrom.Youku,"http://www.soku.com/m/y/video?q=%s");
            put(NetVideoFrom.QQLive,"http://m.v.qq.com/search.html?keyWord=%s");
            put(NetVideoFrom.IQiYi,"http://so.iqiyi.com/so/q_%s");
            put(NetVideoFrom.CloudMusic,"http://music.163.com/api/cloudsearch/get/web?s=%s&limit=50&type=1014&offset=0");
        }
    };
    public static Map<NetVideoFrom, String> videoUrlApi = new HashMap<NetVideoFrom, String>(){
        {
            put(NetVideoFrom.Youku, "http://api.v2.flvurl.cn/parse?single-only=true&appid=6170b6db0a881c18389f47d6d994340e&type=vod&url=%s");
            put(NetVideoFrom.QQLive, "http://api.v2.flvurl.cn/parse?single-only=true&appid=6170b6db0a881c18389f47d6d994340e&type=vod&url=%s");
            put(NetVideoFrom.IQiYi, "http://api.v2.flvurl.cn/parse?single-only=true&appid=6170b6db0a881c18389f47d6d994340e&type=vod&url=%s");
            put(NetVideoFrom.CloudMusic,"http://music.163.com/api/mv/detail?id=%s&type=mp4");
            put(NetVideoFrom.Bilibili, "http://api.v2.flvurl.cn/parse?single-only=true&appid=6170b6db0a881c18389f47d6d994340e&type=vod&url=%s");
        }
    };
    public static Bitmap getURLimage(String url) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    public static String getVideoTitleByUrl(String url, NetVideoFrom type){
        String title = "";
        switch (type){
            case Bilibili:
                title = BilibiliAnalyze.getTitleByUrl(url);
                break;
            case Youku:
                title = YouKuAnalyze.getTitleByUrl(url);
                break;
            case QQLive:
                title = QQLiveAnalyze.getTitleByUrl(url);
                break;
            case IQiYi:
                title = IQiYiAnalyze.getTitleByUrl(url);
                break;
            case YouTube:
                title = YouTubeAnalyze.getTitleByUrl(url);
                break;
        }
        return title;
    }

    public static List<VideoUrlItem> getNetVideoUrlList(NetVideo nv, NetVideoFrom type){
        String api = String.format(videoUrlApi.get(type), nv.getVideoUrl());
        String retStr;
        List<VideoUrlItem> list = new ArrayList<>();
        switch (type){
            case Youku:
                retStr = sendDataByGet(api, "");
                list = YouKuAnalyze.GetVideoUrlList(retStr);
                break;
            case QQLive:
                retStr = sendDataByGet(api, "");
                list = QQLiveAnalyze.GetVideoUrlList(retStr);
                break;
            case IQiYi:
                retStr = sendDataByGet(api, "");
                list = IQiYiAnalyze.GetVideoUrlList(retStr);
                break;
            case Bilibili:
                retStr = sendDataByGet(api, "");
                list = BilibiliAnalyze.GetVideoUrlList(retStr);
                break;
            case CloudMusic:
                if (!nv.getVideoUrl().matches("^\\d+$")){
                    api = CloudMusicAnalyze.getApiByCloudVideoId(nv.getVideoUrl());
                    retStr = sendDataByPost(api);
                    list = CloudMusicAnalyze.GetVideoUrlListSingle(retStr);
                }else {
                    retStr = sendDataByPost(api);
                    list = CloudMusicAnalyze.GetVideoUrlList(retStr);
                }
                break;
        }
        return list;
    }

    public static List<NetVideo> getNetVideoList(String searchStr, NetVideoFrom type) {
        String api = "";
        if (type == NetVideoFrom.Bilibili){
            api = BilibiliAnalyze.getSearchUrl(searchStr);
        }else{
            api = searchAPI.get(type);
        }
        String retStr = "";
        try {
            switch (type){
                case Youku:
                    retStr = sendDataByGet(String.format(api, URLEncoder.encode(searchStr, "utf-8")), "Android");
                    break;
                case QQLive:
                    retStr = sendDataByGet(String.format(api, URLEncoder.encode(searchStr, "utf-8")), "Android");
                    break;
                case Bilibili:
                    retStr = sendDataByGet(api, "Android");
                    break;
                case IQiYi:
                    retStr = sendDataByGet(String.format(api, URLEncoder.encode(searchStr, "utf-8")), "Win32");
                    break;
                case CloudMusic:
                    retStr = sendDataByPost(String.format(api, URLEncoder.encode(searchStr, "utf-8")));
                    break;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return getNetVideoListByJson(retStr,type);
    }



    public static List<NetVideo> getNetVideoListByHome(NetVideoFrom type){
        List<NetVideo> list = new ArrayList<>();
        String api = HomeUrlAPI.get(type);
        String retStr;
        switch (type){
            case Live:
                list = LiveAnalyze.GetVideoListByHome();
                break;
            case Youku:
                retStr = sendDataByGet(api,"Android");
                list = YouKuAnalyze.GetVideoListByHome(retStr);
                break;
            case QQLive:
                retStr = sendDataByGet(api,"Android");
                list = QQLiveAnalyze.GetVideoListByHome(retStr);
                break;
            case Bilibili:
                retStr = sendDataByGet(api,"Win32");
                list = BilibiliAnalyze.GetVideoListByHome(retStr);
                break;
            case IQiYi:
                retStr = sendDataByGet(api,"Win32");
                list = IQiYiAnalyze.GetVideoListByHome(retStr);
                break;
            case CloudMusic:
                retStr =sendDataByPost(api);
                list = CloudMusicAnalyze.GetVideoListByHome(retStr);
                break;
        }
        return list;
    }

    private static List<NetVideo> getNetVideoListByJson(String retStr, NetVideoFrom type){
        List<NetVideo> list = new ArrayList<>();
        switch (type){
            case Youku:
                list = YouKuAnalyze.GetNetVideoList(retStr);
                break;
            case QQLive:
                list = QQLiveAnalyze.GetNetVideoList(retStr);
                break;
            case IQiYi:
                list = IQiYiAnalyze.GetNetVideoList(retStr);
                break;
            case CloudMusic:
                list = CloudMusicAnalyze.GetNetVideoList(retStr);
                break;
            case Bilibili:
                list = BilibiliAnalyze.getNetVideoList(retStr);
        }
        return list;
    }

    @NonNull
    public static String sendDataByGet(String path, String user_agent){
        HttpURLConnection conn;//声明连接对象
        InputStream is;
        StringBuilder resultData = new StringBuilder();
        try {
            URL url = new URL(path); //URL对象
            conn = (HttpURLConnection)url.openConnection(); //使用URL打开一个链接,下面设置这个连接
            conn.setRequestMethod("GET"); //使用get请求
            if (user_agent.length() > 0)
                conn.setRequestProperty("User-agent",user_agent);
            if(conn.getResponseCode()==200){//返回200表示连接成功
                is = conn.getInputStream(); //获取输入流
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader bufferReader = new BufferedReader(isr);
                String inputLine;
                while((inputLine = bufferReader.readLine()) != null){
                    resultData.append(inputLine);
                }
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultData.toString();
    }
    @NonNull
    public static String sendDataByPost(String path){
        HttpURLConnection conn;//声明连接对象
        InputStream is;
        StringBuilder resultData = new StringBuilder();
        try {
            URL url = new URL(path); //URL对象
            conn = (HttpURLConnection)url.openConnection(); //使用URL打开一个链接,下面设置这个连接
            conn.setRequestMethod("POST"); //使用get请求
            int index = path.indexOf(".com");
            if (index > 4) conn.setRequestProperty("referer", path.substring(0, index+4));
            //conn.setRequestProperty("User-agent","Win32");
            if(conn.getResponseCode()==200){//返回200表示连接成功
                is = conn.getInputStream(); //获取输入流
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader bufferReader = new BufferedReader(isr);
                String inputLine;
                while((inputLine = bufferReader.readLine()) != null){
                    resultData.append(inputLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultData.toString();
    }

    public static String GetImagePathByUrl(String imgUrl, File file){
        FileOutputStream fileOutputStream=null;
        //文件夹不存在，则创建它
        if(!file.exists()){
            file.mkdir();
        }

        try {
            URL url = new URL(imgUrl);

            fileOutputStream=new FileOutputStream(file.getPath()+"/"+System.currentTimeMillis() + imgUrl.substring(imgUrl.length()-10,imgUrl.length()-4) +".png");
            (BitmapFactory.decodeStream(url.openStream())).compress(Bitmap.CompressFormat.PNG, 100,fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getPath()+"/"+System.currentTimeMillis() + imgUrl.substring(imgUrl.length()-10,imgUrl.length()-4) +".png";
    }

    public static String getVideoUrlFromThird(String url){
        if (url.contains("m.youku.com/video")){
            url = url.replace("m.youku.com/video","v.youku.com/v_show");
        }
        if (url.contains("m.v.qq.com/x/cover/g/"))
            url = url.replace("http://m.v.qq.com/x/cover/g/","https://v.qq.com/x/cover/");
        int len = url.indexOf("?");
        if (len > 0) url = url.substring(0,len);
        String hashStr = sendDataByGet("https://www.parsevideo.com", "Win32");
        String hash = Tools.getStrWithRegular("var\\W+hash\\W+=\\W+\"([^\"]+)\";",hashStr);
        String anaStr = sendDataByGet("https://www.parsevideo.com/api.php?hash="+hash+"&url=" + url, "Win32");
        String videoUrl = "";
        try {
            int total =  new JSONObject(anaStr).getInt("total");
            if (total > 0){
                videoUrl = new JSONObject(anaStr).getJSONArray("video").getJSONObject(0).getString("url");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return videoUrl;
    }
    public static String getVideoUrlFrom163ren(String url){
        String anaStr = sendDataByGet("http://jx.api.163ren.com/vod.php?url=" + url, "Win32");
        String param = Tools.getStrWithRegular("url\\s+: '([\\s\\S][^']+)',",anaStr);
        anaStr = sendDataByGet("http://jx.api.163ren.com/api.php?url=" + param, "Win32");
        String videoUrl = "http" + Tools.getStrWithRegular("http([\\s\\S][^']+)]]",anaStr);
        return videoUrl;
    }
    public static String getVideoUrlFromBiliBili(String url){
        return BilibiliAnalyze.getVideoUrl(url);
    }
    public static boolean checkWebViewUrl(String url, NetVideoFrom type){
        switch (type){
            case Youku:
                return YouKuAnalyze.checkVideoUrl(url);
            case Bilibili:
                return BilibiliAnalyze.checkVideoUrl(url);
            case QQLive:
                return QQLiveAnalyze.checkVideoUrl(url);
            case IQiYi:
                return IQiYiAnalyze.checkVideoUrl(url);
            case YouTube:
                return YouTubeAnalyze.checkVideoUrl(url);
            default:
                return false;
        }
    }

    public static String getPageLoadedJs(NetVideoFrom type) {
        switch (type){
            case Youku:
                return YouKuAnalyze.getPageLoadedJs();
            case Bilibili:
                return BilibiliAnalyze.getPageLoadedJs();
            case QQLive:
                return QQLiveAnalyze.getPageLoadedJs();
            case IQiYi:
                return IQiYiAnalyze.getPageLoadedJs();
            case YouTube:
                return YouTubeAnalyze.getPageLoadedJs();
            default:
                return "";
        }
    }

    public static String getVideoUrl(String url, NetVideoFrom type) {
        String videoUrl = "";
        switch (type){
            case Youku:
                videoUrl = getVideoUrlFromThird(url);
                break;
            case Bilibili:
                videoUrl = getVideoUrlFromBiliBili(url);
                break;
            case IQiYi:
                url = url.replace("//m.","//www.");
                videoUrl = getVideoUrlFromThird(url);
                break;
            case QQLive:
                url = url.substring(0, url.indexOf(".html")+5).replace("m.v.qq.com/x/cover/q/", "v.qq.com/x/cover/");
                videoUrl = getVideoUrlFromThird(url);
                break;
            case YouTube:
                videoUrl = YouTubeAnalyze.getVideoUrl(url);
                break;
            default:
                videoUrl = getVideoUrlFromThird(url);
                break;
        }
        return videoUrl;
    }

    public static boolean getIsShield(String url, NetVideoFrom type){
        switch (type){
            case Youku:
                return YouKuAnalyze.isShield(url);
            case Bilibili:
                return BilibiliAnalyze.isShield(url);
            case QQLive:
                return QQLiveAnalyze.isShield(url);
            case IQiYi:
                return IQiYiAnalyze.isShield(url);
            case YouTube:
                return YouTubeAnalyze.isShield(url);
            default:
                return false;
        }
    }
}
