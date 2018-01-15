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


public class YouKuAnalyze {
    public static List<NetVideo> GetVideoListByHome(String retStr){
        List<NetVideo> list =  new ArrayList<>();
        String pattern = "<body[^>]*>([\\s\\S]*)</body>";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(retStr);
        if(m.find()) {
            retStr = m.group(0);
        }
        Document doc = Jsoup.parse(retStr);
        Elements links = doc.select("a.v-link, a.p-link");
        for ( Element link: links) {
            Element img = link.previousElementSibling();
            int length = img.attr("style").length();
            String imgUrl = img.attr("style").substring(22, length-3);
            if (imgUrl.startsWith("//")){
                imgUrl = imgUrl.replaceFirst("//", "http://");
            }else if(imgUrl.startsWith("https://")){

            }else{
                imgUrl = "";
            }
            list.add(new NetVideo(
                    link.attr("title"),
                    "",
                    imgUrl,
                    link.attr("href").replaceFirst("//", "http://"),
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
        Elements yk_dir_container = doc.getElementsByClass("yk_dir");
        Document yk_dir_containerDoc = Jsoup.parse(yk_dir_container.toString());
        Elements yk_dir = yk_dir_containerDoc.select("div.yk_card,div.ep_box");

        for(Element yk_dir_div: yk_dir) {
            if (yk_dir_div.attr("id").equals("more-video")) continue;
            if(yk_dir_div.hasClass("yk_card")) {
                Element item = yk_dir_div.getElementsByClass("card_link").first();
                Element img = yk_dir_div.getElementsByTag("img").first();
                if (item.attr("href").contains("i.youku.com")) continue;
                list.add(new NetVideo(
                        item.attr("_log_title"),
                        "",
                        img != null ? img.attr("src").replaceFirst("//", "http://") : "",
                        item.attr("href").replaceFirst("//", "http://"),
                        "" ));
            }else if(yk_dir_div.hasClass("ep_box")){
                Elements clearfix = yk_dir_div.select("li");
                for(int i=0;i<clearfix.size();i++){
                    Document clearfixliDoc = Jsoup.parse(clearfix.get(i).toString());
                    Elements item = clearfixliDoc.select("a");  //选择器的形式
                    Document span = Jsoup.parse(item.toString());
                    Elements spana = span.select("span");
                    list.add(new NetVideo(
                            item.text().replaceFirst(spana.text(), ""),
                            item.attr("_log_title"),
                            "",
                            item.attr("href").replaceFirst("//", "http://"),
                            i+1+""));
                }
            }
        }
        Elements yk_result_container = doc.getElementsByClass("yk_result _sk_content");
        Elements yk_result = yk_result_container.select(".v");
        for(Element yk_result_v: yk_result) {
            Element item = yk_result_v.select("a").first();
            Element img = yk_result_v.getElementsByTag("img").first();
            Element subTitle = yk_result_v.getElementsByClass("v-desc-col").first();

            list.add(new NetVideo(
                    item.attr("title"),
                    subTitle.text(),
                    img.attr("src").replaceFirst("//", "http://"),
                    item.attr("href").replaceFirst("//", "http://"),
                    ""));
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
                    if (stream.getString("type").equals("M3U8") && stream.getString("subType").equals("MP4")){
                        String des = VideoUrlTypr.get(stream.getString("quality"));
                        String url = ((JSONArray)(stream.get("segs"))).getJSONObject(0).getString("url");
                        VideoUrlItem vui = new VideoUrlItem(des,url );
                        list.add(vui);
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