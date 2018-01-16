package com.exer.videoapi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.exer.videoapi.anlysis.IQiYiAnalize;
import com.exer.videoapi.anlysis.QQLiveAnalyze;
import com.exer.videoapi.anlysis.YouKuAnalyze;
import com.exer.widgets.VideoUrlItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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

public class NetVideoHelper {
    public static Map<String, String> VideoUrlTypr = new HashMap<String, String>(){
        {
            put("SPEED", "极速");
            put("SD", "标清");
            put("HD", "高清");
            put("SUPERHD", "超清");
            put("1080P", "1080P");
        }
    };
    private static Map<NetVideoFrom, String> HomeUrlAPI = new HashMap<NetVideoFrom, String>(){
        {
            put(NetVideoFrom.Youku,"http://www.youku.com");
            put(NetVideoFrom.QQLive,"http://m.v.qq.com");
            put(NetVideoFrom.IQiYi,"http://m.iqiyi.com/");
        }
    };
    private static Map<NetVideoFrom, String> searchAPI = new HashMap<NetVideoFrom, String>(){
        {
            //put(NetVideoFrom.Youku,"https://openapi.youku.com/v2/searches/video/by_keyword.json?count=20&client_id=53e6cc67237fc59a&page=1&keyword=%s");http://www.soku.com/m/y/video?q=
            put(NetVideoFrom.Youku,"http://www.soku.com/m/y/video?q=%s");
            put(NetVideoFrom.QQLive,"http://m.v.qq.com/search.html?keyWord=%s");
            put(NetVideoFrom.IQiYi,"http://so.iqiyi.com/so/q_%s");
        }
    };
    private static Map<NetVideoFrom, String> videoUrlApi = new HashMap<NetVideoFrom, String>(){
        {
            put(NetVideoFrom.Youku, "http://api.v2.flvurl.cn/parse?single-only=true&appid=6170b6db0a881c18389f47d6d994340e&type=vod&url=%s");
            put(NetVideoFrom.QQLive, "http://api.v2.flvurl.cn/parse?single-only=true&appid=6170b6db0a881c18389f47d6d994340e&type=vod&url=%s");
            put(NetVideoFrom.IQiYi, "http://api.v2.flvurl.cn/parse?single-only=true&appid=6170b6db0a881c18389f47d6d994340e&type=vod&url=%s");
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

    public static List<VideoUrlItem> getNetVideoUrlList(NetVideo nv, NetVideoFrom type) {
        String api = String.format(videoUrlApi.get(type), nv.getVideoUrl());
        String retStr = sendDataByGet(api, "");


        return getVideoUrlListByJson(retStr,type);
    }

    private static List<VideoUrlItem> getVideoUrlListByJson(String retStr, NetVideoFrom type){
        List<VideoUrlItem> list = new ArrayList<>();
        switch (type){
            case Youku:
                list = YouKuAnalyze.GetVideoUrlList(retStr);
                break;
            case QQLive:
                list = QQLiveAnalyze.GetVideoUrlList(retStr);
                break;
            case IQiYi:
                list = IQiYiAnalize.GetVideoUrlList(retStr);
                break;
        }
        return list;
    }

    public static List<NetVideo> getNetVideoList(String searchStr, NetVideoFrom type) {
        String api = searchAPI.get(type);
        String retStr = "";
        try {
            switch (type){
                case Youku:
                    retStr = sendDataByGet(String.format(api, URLEncoder.encode(searchStr, "utf-8")), "Android");
                    break;
                case QQLive:
                    retStr = sendDataByGet(String.format(api, URLEncoder.encode(searchStr, "utf-8")), "Android");
                    break;
                case IQiYi:
                    retStr = sendDataByGet(String.format(api, URLEncoder.encode(searchStr, "utf-8")), "Win32");
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
            case Youku:
                retStr = sendDataByGet(api,"Android");
                list = YouKuAnalyze.GetVideoListByHome(retStr);
                break;
            case QQLive:
                retStr = sendDataByGet(api,"Android");
                list = QQLiveAnalyze.GetVideoListByHome(retStr);
                break;
            case IQiYi:
                retStr = sendDataByGet(api,"Win32");
                list = IQiYiAnalize.GetVideoListByHome(retStr);
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
                list = IQiYiAnalize.GetNetVideoList(retStr);
                break;
        }
        return list;
    }

    @NonNull
    private static String sendDataByGet(String path, String user_agent){
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
    private static String sendDataByPost(String path){
        HttpURLConnection conn;//声明连接对象
        InputStream is;
        StringBuilder resultData = new StringBuilder();
        try {
            URL url = new URL(path); //URL对象
            conn = (HttpURLConnection)url.openConnection(); //使用URL打开一个链接,下面设置这个连接
            conn.setRequestMethod("Post"); //使用get请求
            conn.setRequestProperty("User-agent","Mozilla/5.0(Linux;U;Android2.2.1;zh-cn;HTC_Wildfire_A3333Build/FRG83D)AppleWebKit/533.1(KHTML,likeGecko)Version/4.0MobileSafari/533.1");
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
//    https://openapi.youku.com/v2/searches/video/by_keyword.json?count=20&client_id=53e6cc67237fc59a&page=1&keyword=%E8%A2%81%E8%85%BE%E9%A3%9E
//    URL url = new URL("http://images.csdn.net/20130609/zhuanti.jpg");
//    mImageView.setImageBitmap(BitmapFactory.decodeStream(url.openStream()));

}
