package com.exer.videoapi.anlysis;

import com.exer.videoapi.NetVideoHelper;
import com.exer.widgets.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by HeHang on 2018/2/6.
 */

public class YouTubeAnalyze {
    public static String localUrl = "";
    public static String data = "";
    public static String getTitleByUrl(String url) {
        localUrl = url;
        String title = "";
        String vid = Tools.getStrWithRegular("/watch\\?[\\s\\S]+v=([\\s\\S]+)",url);
        String anaStr = NetVideoHelper.sendDataByGet("https://www.youtube.com/get_video_info?video_id=" + vid, "Win32");
        try {
            for (int i=0; i < 3;i++) anaStr = URLDecoder.decode(anaStr, "utf-8");
            data = anaStr;
            title = Tools.getStrWithRegular("title=([^&]*)&",data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return title;
    }
    public static String getVideoUrl(String url){
        if (url.equals(localUrl) && data.length() > 0){
            List<String> list = Tools.getStrListWithRegular("codecs=[^;]*video/mp4;",data);
            for(int i=0; i < list.size(); i++){
                list.set(i,Tools.getStrWithRegular("url=([^;]*video/mp4);",list.get(i)));
            }
            if (list.size() > 0)
                return list.get(0);
            else
                return "";
        }else{
            String vid = Tools.getStrWithRegular("/watch\\?[\\s\\S]+v=([\\s\\S]+)",url);
            String anaStr = NetVideoHelper.sendDataByGet("https://www.youtube.com/get_video_info?video_id=" + vid, "Win32");
            try {
                for (int i=0; i < 3;i++) data = URLDecoder.decode(anaStr, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            List<String> list = Tools.getStrListWithRegular("codecs=[^;]*video/mp4;",data);
            for(int i=0; i < list.size(); i++){
                list.set(i,Tools.getStrWithRegular("url=([^;]*video/mp4);",list.get(i)));
            }
            if (list.size() > 0)
                return list.get(0);
            else
                return "";
        }
    }

    public static boolean checkVideoUrl(String url) {
        return url.contains("/watch?v=") || Tools.getStrWithRegular("/watch\\?[\\s\\S]+v=([\\s\\S]+)",url).length() > 0;
    }

    public static String getPageLoadedJs() {
        return "function closeAd(){function clk(event){return null;};if(document.getElementsByClassName('ptb cgb')[0].getElementsByClassName('hmb')[0].innerHTML.indexOf('广告') != -1) document.getElementsByClassName('ptb cgb')[0].style.display='none';};closeAd();";
    }

    private static String shieldUrls = "";
    public static boolean isShield(String url) {
        return  shieldUrls.contains(url) || url.contains(".apk") || url.contains("youtube.com/feed?");
    }

    public static String[] getVideoInfoByHtml(String html){
        List<String> list = Tools.getStrListWithRegular("<tr>[\\s\\S]+?</tr>", html);
        for(int i=0; i < list.size(); ){
            if(!(list.get(i).contains("googlevideo") && Pattern.matches("[\\s\\S]+\\([\\d]+p\\)[\\s]+.mp4[\\s\\S]+", list.get(i)))){
                list.remove(i);
            }else{
                i++;
            }
        }
        for(int i=list.size()-1; i >= 0; i--){
            if(list.get(i).contains("(720p) .mp4") || list.get(i).contains("(480p) .mp4")){
                String title = Tools.getStrWithRegular("<h3>([\\s\\S]+?)</h3>",html);
                String url = Tools.getStrWithRegular("href=([\\s\\S]+?) ",list.get(i));
                return new String[]{title,url};
            }
        }
        return new String[1];
    }
}
