package com.exer.videoapi.anlysis;

import com.exer.videoapi.NetVideo;
import com.exer.widgets.VideoUrlItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    String videoInfo = stream.getString("aid");
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

    public static List<NetVideo> GetNetVideoList(String retStr){
        List<NetVideo> list =  new ArrayList<>();
        String pattern = "<body[^>]*>([\\s\\S]*)</body>";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(retStr);
        if(m.find()) {
            retStr = m.group(0);
        }
        Document doc = Jsoup.parse(retStr);
        Elements albun_links = doc.select("a.album_link");


        for(int i = 0; i < albun_links.size(); i++){
            Element link = albun_links.get(i);
            if (link.hasClass("album_link-more") || link.attr("title").equals("更多")) continue;
            list.add(new NetVideo(
                    link.attr("title"),
                    "",
                    "",
                    link.attr("href"),
                    ""+(i+1) ));
        }

        int i = 1;
        Elements links = doc.select("a.figure");
        for ( Element link: links) {
            if (!link.attr("href").contains(".html")) continue;
            Element img = link.selectFirst("img");
            list.add(new NetVideo(
                    img.attr("title"),
                    "",
                    img.attr("src"),
                    link.attr("href"),
                    "1"));
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
}
