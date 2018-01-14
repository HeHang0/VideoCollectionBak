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


public class IQiYiAnalize {
    public static List<NetVideo> GetVideoListByHome(String retStr){
        List<NetVideo> list =  new ArrayList<>();
//        String pattern = "<body[^>]*>([\\s\\S]*)</body>";
//        Pattern r = Pattern.compile(pattern);
//        Matcher m = r.matcher(retStr);
//        if(m.find()) {
//            retStr = m.group(0);
//        }
        Document doc = Jsoup.parse(retStr);

        Elements links0 = doc.select("a.mod_focus-index_item");
        for ( Element link: links0) {
            Element img = link.parent();
            String imgUrl = img.attr("data-indexfocus-lazyimg");
            if (imgUrl.length()< 1) imgUrl = img.attr("style").replace("background-image: url(","").replaceFirst("\\);([\\w\\W]*)","");
            imgUrl = imgUrl.startsWith("//") ? "http:"+imgUrl : imgUrl;
            String url = link.attr("href");
            url = url.startsWith("//") ? "http:"+url : url;
            list.add(new NetVideo(
                    link.attr("alt"),
                    "",
                    imgUrl,
                    url,
                    "1" ));
        }

        Elements links = doc.select("a.site-piclist_pic_link");
        for ( Element link: links) {
            Element img = link.selectFirst("img");
            String imgUrl = img.attr("src");
            imgUrl = imgUrl.startsWith("//") ? "http:"+imgUrl : imgUrl;
            String url = link.attr("href");
            url = url.startsWith("//") ? "http:"+url : url;
            list.add(new NetVideo(
                    link.attr("title").length() < 1 ? img.attr("title") : link.attr("title"),
                    "",
                    imgUrl,
                    url,
                    "1" ));
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
            if (link.attr("title").equals("更多")) continue;
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
                    if (stream.getString("type").equals("M3U8")){
                        String des = VideoUrlTypr.get(stream.getString("quality"));
                        JSONArray ja = (JSONArray)(stream.get("segs"));
                        if (ja.length() == 1){
                            String url = ja.getJSONObject(0).getString("url");
                            VideoUrlItem vui = new VideoUrlItem(des,url );
                            list.add(vui);
                        }
                    }
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
