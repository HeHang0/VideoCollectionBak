package com.exer.videoapi.anlysis;

import com.exer.videoapi.NetVideo;

import java.util.ArrayList;
import java.util.List;


public class LiveAnalyze {
    public static List<NetVideo> GetVideoListByHome(){
        List<NetVideo> list =  new ArrayList<>();
        list.add(new NetVideo("CCTV1","高清","http://download.easyicon.net/png/518254/77/","http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8","1"));
        list.add(new NetVideo("CCTV3","高清","http://download.easyicon.net/png/518256/77/","http://ivi.bupt.edu.cn/hls/cctv3hd.m3u8","2"));
        list.add(new NetVideo("CCTV5","高清","http://download.easyicon.net/png/518258/77/","http://ivi.bupt.edu.cn/hls/cctv5hd.m3u8","3"));
        list.add(new NetVideo("CCTV5+","高清","http://download.easyicon.net/png/518258/77/","http://ivi.bupt.edu.cn/hls/cctv5phd.m3u8","4"));
        list.add(new NetVideo("CCTV6","高清","http://download.easyicon.net/png/518259/77/","http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8","5"));
        list.add(new NetVideo("CCTV9","标清","http://download.easyicon.net/png/518259/77/","http://223.82.250.72/ysten-business/live/cctv-news/1.m3u8","6"));
        list.add(new NetVideo("香港卫视","高清","http://www.hkstv.tv/templates/site_shared/assets/img/blogo.png","http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8","6"));
        return list;
    }
}
