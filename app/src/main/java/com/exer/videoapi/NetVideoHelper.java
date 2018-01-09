package com.exer.videoapi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HeHang on 2018/1/8.
 */

public class NetVideoHelper {
    private static Map<NetVideoFrom, String> searchAPI = new HashMap<NetVideoFrom, String>(){
        {
            put(NetVideoFrom.Youku,"https://openapi.youku.com/v2/searches/video/by_keyword.json?count=20&client_id=53e6cc67237fc59a&page=1&keyword=%s");
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
    public static List<NetVideo> getNetVideoList(String searchStr, NetVideoFrom type) {
        String api = searchAPI.get(type);
        String retStr = "";
        try {
            retStr = sendDataByGet(String.format(api, URLEncoder.encode(searchStr, "utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return getNetVideoListByJson(retStr,type);
    }

    private static List<NetVideo> getNetVideoListByJson(String jsonStr, NetVideoFrom type){
        List<NetVideo> list = new ArrayList<>();
        switch (type){
            case Youku:
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONArray videos = (JSONArray) jsonObject.get("videos");
                    for (int i = 0; i < videos.length(); i++){
                        try {
                            JSONObject video = videos.getJSONObject(i);
                            URL url = new URL(video.getString("thumbnail"));
                            NetVideo nv = new NetVideo(video.getString("title"), video.getString("category"), BitmapFactory.decodeStream(url.openStream()), video.getString("link"));
                            list.add(nv);
                        } catch (JSONException|IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        return list;
    }

    private static String sendDataByGet(String path){
        HttpURLConnection conn;//声明连接对象
        InputStream is;
        StringBuilder resultData = new StringBuilder();
        try {
            URL url = new URL(path); //URL对象
            conn = (HttpURLConnection)url.openConnection(); //使用URL打开一个链接,下面设置这个连接
            conn.setRequestMethod("GET"); //使用get请求

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
